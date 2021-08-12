import { Button, List, Space, Typography } from "antd";
import React from "react";
import {
  useGetCartQuery,
  useRemoveSampleMutation,
} from "../../../../apis/cart/cart";
import { useGetSampleIdsForProjectQuery } from "../../../../apis/projects/project";
import { IconRemove } from "../../../../components/icons/Icons";
import { SampleDetailViewer } from "../../../../components/samples/SampleDetailViewer";
import { grey3 } from "../../../../styles/colors";

export function ShareSamples({ projectId }) {
  const [samples, setSamples] = React.useState();
  const [existing, setExisting] = React.useState();
  const [removeSample] = useRemoveSampleMutation();

  const { data: allSamples, isLoading: isSamplesLoading } = useGetCartQuery();
  const { data: ids, isLoading: idsLoading } = useGetSampleIdsForProjectQuery(
    projectId,
    {
      skip: projectId === undefined,
    }
  );

  React.useEffect(() => {
    const updatedSamples = [];
    const updateExisting = [];
    if (!isSamplesLoading && !idsLoading) {
      console.log("UPDATING - INSIDE");
      console.log(allSamples);
      allSamples.forEach((sample) => {
        // Find out if sample is in the selected project
        if (ids.includes(Number(sample.id))) {
          updateExisting.push(sample);
        } else {
          updatedSamples.push(sample);
        }
      });
    }
    setSamples(updatedSamples);
    setExisting(updateExisting);
  }, [allSamples, ids, idsLoading, isSamplesLoading]);

  return (
    <div>
      <Typography.Title level={4}>Available Samples</Typography.Title>
      <Space direction="vertical" style={{ display: "block" }}>
        <List
          loading={isSamplesLoading}
          bordered
          dataSource={samples}
          renderItem={(sample) => (
            <List.Item
              actions={[
                <Button
                  type="text"
                  key="remove"
                  icon={<IconRemove />}
                  size="small"
                  onClick={() => removeSample({ sampleId: sample.id })}
                />,
              ]}
            >
              <SampleDetailViewer
                sampleId={sample.id}
                removeSample={removeSample}
              >
                <Button size="small">{sample.label}</Button>
              </SampleDetailViewer>
            </List.Item>
          )}
        />
        {existing?.length > 0 && (
          <>
            <Typography.Text>
              Note: the following examples already exist in the destination
              project and will not be shared
            </Typography.Text>
            <List
              loading={isSamplesLoading}
              bordered
              dataSource={existing}
              renderItem={(sample) => (
                <List.Item
                  style={{
                    backgroundColor: grey3,
                  }}
                >
                  {sample.label}
                </List.Item>
              )}
            />
          </>
        )}
      </Space>
    </div>
  );
}
