.DEFAULT_GOAL := help

ifneq (,$(wildcard .env))
include .env
export
endif

ROOT := $(shell pwd)
MAVEN_IMAGE := maven:3.9-eclipse-temurin-21
NODE_IMAGE := node:22-alpine
MAVEN_CACHE := $(HOME)/.m2
TEST_NET := notification-system-test-net
DIND_NAME := notification-system-dind
DIND_IMAGE := docker:27-dind

BACKEND_RUN := docker run --rm -v $(ROOT)/backend:/app -v $(MAVEN_CACHE):/root/.m2 -w /app $(MAVEN_IMAGE)
BACKEND_TEST_RUN := docker run --rm \
	-v $(ROOT)/backend:/app \
	-v $(MAVEN_CACHE):/root/.m2 \
	--network $(TEST_NET) \
	-e DOCKER_HOST=tcp://$(DIND_NAME):2375 \
	-e TESTCONTAINERS_HOST_OVERRIDE=$(DIND_NAME) \
	-e TESTCONTAINERS_RYUK_DISABLED=true \
	-w /app $(MAVEN_IMAGE)
FRONTEND_RUN := docker run --rm -v $(ROOT)/frontend:/app -w /app $(NODE_IMAGE)

help:
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "  %-22s %s\n", $$1, $$2}'

build: build-backend build-frontend ## Build backend jar and frontend bundle

build-backend: ## Build backend jar (mvn package)
	$(BACKEND_RUN) mvn -B -DskipTests package

build-frontend: ## Install deps and build frontend bundle
	$(FRONTEND_RUN) sh -c "npm ci --maxsockets=3 --fetch-retries=5 --fetch-retry-mintimeout=20000 --fetch-retry-maxtimeout=120000 --fetch-timeout=600000 && npm run build"

test: test-backend ## Run all tests

test-backend: ## Run backend unit + integration tests (Testcontainers via dind)
	@docker network create $(TEST_NET) >/dev/null 2>&1 || true
	@docker rm -f $(DIND_NAME) >/dev/null 2>&1 || true
	@docker run -d --rm --privileged --name $(DIND_NAME) \
		--network $(TEST_NET) \
		-e DOCKER_TLS_CERTDIR= \
		$(DIND_IMAGE) --host=tcp://0.0.0.0:2375 >/dev/null
	@printf "Waiting for dind to start"; \
		for i in $$(seq 1 30); do \
			if docker exec $(DIND_NAME) docker info >/dev/null 2>&1; then echo " ready."; break; fi; \
			printf "."; sleep 1; \
		done
	@trap 'docker rm -f $(DIND_NAME) >/dev/null 2>&1; docker network rm $(TEST_NET) >/dev/null 2>&1' EXIT; \
		$(BACKEND_TEST_RUN) mvn -B test

fmt: ## Apply google-java-format to backend sources
	$(BACKEND_RUN) mvn -B com.spotify.fmt:fmt-maven-plugin:format

fmt-check: ## Verify backend sources are formatted
	$(BACKEND_RUN) mvn -B com.spotify.fmt:fmt-maven-plugin:check

frontend-install: ## Install frontend dependencies
	$(FRONTEND_RUN) npm install

frontend-typecheck: ## TypeScript type-check the frontend
	$(FRONTEND_RUN) sh -c "npm run typecheck"

frontend-lint: ## ESLint the frontend
	$(FRONTEND_RUN) sh -c "npm run lint"

up: ## Start backend + frontend with docker compose
	docker compose up -d --build

down: ## Stop and remove containers
	docker compose down

logs: ## Tail logs from running services
	docker compose logs -f

ps: ## Show running services
	docker compose ps

demo: up ## Start the full stack and tail logs
	@echo "Backend:  http://localhost:8080"
	@echo "Frontend: http://localhost:5173"
	@docker compose logs -f

mvn: ## Run an arbitrary maven command (use ARGS="...")
	$(BACKEND_RUN) mvn $(ARGS)

npm: ## Run an arbitrary npm command (use ARGS="...")
	$(FRONTEND_RUN) npm $(ARGS)

clean: ## Remove build artifacts
	rm -rf backend/target frontend/dist frontend/node_modules

rabbit-ui: ## Open RabbitMQ management UI in the browser
	@echo "RabbitMQ management: http://localhost:15672 (guest/guest)"
	@command -v open >/dev/null && open http://localhost:15672 || true

logs-rabbit: ## Tail RabbitMQ logs
	docker compose logs -f rabbitmq

rabbit-cli: ## Run rabbitmqctl inside the broker (use ARGS="...")
	docker compose exec rabbitmq rabbitmqctl $(ARGS)

rabbit-purge: ## Purge the dispatch dead-letter queue
	docker compose exec rabbitmq rabbitmqctl purge_queue notifications.dispatch.dlq

db-shell: ## Open psql against the postgres container
	docker compose exec -e PGPASSWORD=$${POSTGRES_PASSWORD:-notif} postgres \
		psql -U $${POSTGRES_USER:-notif} -d $${POSTGRES_DB:-notifications}

logs-postgres: ## Tail postgres logs
	docker compose logs -f postgres

.PHONY: help build build-backend build-frontend test test-backend fmt fmt-check \
	frontend-install frontend-typecheck frontend-lint up down logs ps demo mvn npm clean \
	rabbit-ui logs-rabbit rabbit-cli rabbit-purge db-shell logs-postgres
