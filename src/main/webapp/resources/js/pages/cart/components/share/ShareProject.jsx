import { Form, Select, Space, Spin, Tag, Tooltip } from "antd";
import React from "react";
import { useDispatch, useSelector } from "react-redux";
import { useGetCartQuery } from "../../../../apis/cart/cart";
import { useGetProjectsForUserQuery } from "../../../../apis/projects/projects";
import { IconShoppingCart } from "../../../../components/icons/Icons";
import { ShareSamples } from "./ShareSamples";
import { setProject } from "./shareSlice";

/**
 * React component to render a select field for selection of the project
 * to share samples with.
 * @returns {JSX.Element}
 * @constructor
 */
export function ShareProject() {
  const project = useSelector((state) => state.share.project);
  const { data: samples = [] } = useGetCartQuery();
  const [options, setOptions] = React.useState();

  const [query, setQuery] = React.useState("");
  const dispatch = useDispatch();

  const { data: projects = [], isFetching } = useGetProjectsForUserQuery(query);

  const setValue = (newValue) => {
    const project = projects.find((project) => project.identifier === newValue);
    dispatch(setProject(project));
  };

  React.useEffect(() => {
    if (!isFetching) {
      setOptions(
        projects.map((project) => ({
          label: (
            <div
              style={{
                display: "flex",
                justifyContent: "space-between",
                alignContent: "center",
              }}
            >
              {project.name}
              {samples.findIndex(
                (s) => s.project.id === Number(project.identifier)
              ) > -1 && (
                <span>
                  <Tooltip
                    placement="left"
                    title={i18n("ShareProject.samples-in-cart")}
                  >
                    <Tag color="blue">
                      <IconShoppingCart />
                    </Tag>
                  </Tooltip>
                </span>
              )}
            </div>
          ),
          value: project.identifier,
        }))
      );
    }
  }, [isFetching, projects, samples]);

  return (
    <Space direction="vertical" style={{ display: "block" }}>
      <Form.Item label={i18n("ShareProject.label")}>
        <Select
          autoFocus
          size="large"
          value={project?.identifier}
          onChange={setValue}
          options={options}
          showSearch
          onSearch={setQuery}
          style={{ width: `100%` }}
          filterOption={false}
          notFoundContent={isFetching ? <Spin size="small" /> : null}
        />
      </Form.Item>

      {project && <ShareSamples projectId={project?.identifier} />}
    </Space>
  );
}
