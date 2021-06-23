import { Select, Table } from "antd";
import React from "react";
import {
  getMetadataRestrictions,
  useGetMetadataFieldsForProjectQuery,
} from "../../../../apis/metadata/field";
import { blue6, green6 } from "../../../../styles/colors";

export function ShareMetadataFields({ projectId, sharedProjectId }) {
  const [fields, setFields] = React.useState();
  const [restrictions, setRestrictions] = React.useState([]);

  const ROLES = {
    PROJECT_USER: i18n("projectRole.PROJECT_USER"),
    PROJECT_OWNER: i18n("projectRole.PROJECT_OWNER"),
  };

  const {
    data: sharedFields,
    isLoading: sharedLoading,
  } = useGetMetadataFieldsForProjectQuery(projectId);
  const {
    data: projectFields,
    isLoading: projectsLoading,
  } = useGetMetadataFieldsForProjectQuery(sharedProjectId);

  React.useEffect(() => {
    getMetadataRestrictions().then(setRestrictions);
  }, []);

  React.useEffect(() => {
    if (!sharedLoading && !projectsLoading) {
      const merged = sharedFields.map((current) => {
        const target = projectFields.find(
          (element) => element.label === current.label
        );
        return {
          current,
          target:
            target !== undefined
              ? { ...target, exists: true }
              : { ...current, exists: false },
        };
      });
      setFields(merged);
    }
  }, [projectFields, projectsLoading, sharedFields, sharedLoading]);

  return (
    <div>
      <Table
        rowKey={(item) => `field-${item.current.id}`}
        columns={[
          { title: "Field Label", dataIndex: ["current", "label"] },
          {
            title: "Current Project Restrictions",
            dataIndex: ["current", "restriction"],
            render: (text) => ROLES[text],
          },
          {
            title: "Target Project Restrictions",
            dataIndex: ["target", "restriction"],
            render: (text, item) => {
              console.log({ text, item });
              return (
                <div
                  style={{
                    backgroundColor: item.target.exists ? blue6 : green6,
                  }}
                >
                  <Select style={{ display: "block" }} defaultValue={text}>
                    {restrictions.map((restriction) => (
                      <Select.Option value={restriction.value}>
                        {restriction.text}
                      </Select.Option>
                    ))}
                  </Select>
                </div>
              );
            },
          },
        ]}
        dataSource={fields}
      />
    </div>
  );
}
