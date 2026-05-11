interface UserSelectorProps {
  userId: number;
  onChange: (userId: number) => void;
}

export function UserSelector({ userId, onChange }: UserSelectorProps) {
  return (
    <div className="row">
      <label htmlFor="user-id">Active user</label>
      <input
        id="user-id"
        type="number"
        min={1}
        value={userId}
        onChange={(e) => onChange(Number(e.target.value) || 1)}
      />
    </div>
  );
}
