import { Select, Space, Typography } from "antd";
import React from "react";
import { useDispatch } from "react-redux";
import { setProject } from "./shareSlice";

/**
 * React component for selecting the project to share a sample with.
 * @returns {JSX.Element}
 * @constructor
 */
export function ShareProject({ projects = [] }) {
  const dispatch = useDispatch();
  const [options, setOptions] = React.useState();

  React.useEffect(() => {
    setOptions(
      projects.map((project) => ({
        label: project.name,
        value: project.identifier,
      }))
    );
  }, [projects]);

  return (
    <Space direction="vertical" style={{ display: "block" }}>
      <Typography.Text strong>{i18n("ShareSamples.projects")}</Typography.Text>
      <Select
        autoFocus
        size="large"
        style={{ width: `100%` }}
        options={options}
        onChange={(projectId) => dispatch(setProject(projectId))}
      />
    </Space>
  );
}
