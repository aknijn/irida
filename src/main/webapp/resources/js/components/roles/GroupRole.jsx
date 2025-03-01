import { notification, Select } from "antd";
import React from "react";
import { useRoles } from "../../contexts/roles-context";

/**
 * React component for selecting a user group role for a member of a group
 *
 * @param item
 * @param canManage
 * @param updateRoleFn
 * @returns {JSX.Element|*}
 * @constructor
 */
export function GroupRole({ item, canManage, updateRoleFn }) {
  const [role, setRole] = React.useState(item.role);
  const [loading, setLoading] = React.useState(false);

  const { roles, getRoleFromKey } = useRoles();

  const onChange = (value) => {
    setLoading(true);
    updateRoleFn({ userId: item.id, role: value })
      .then((message) => {
        notification.success({ message });
        setRole(value);
      })
      .catch((message) => notification.error({ message }))
      .finally(() => setLoading(false));
  };

  return canManage ? (
    <Select
      value={role}
      onChange={onChange}
      style={{ width: "100%" }}
      loading={loading}
      disabled={loading}
    >
      {roles.map((role) => (
        <Select.Option value={role.value} key={role.value}>
          {role.label}
        </Select.Option>
      ))}
    </Select>
  ) : (
    getRoleFromKey(item.role)
  );
}
