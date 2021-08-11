import { List, Typography } from "antd";
import React from "react";
import { useGetCartSamplesQuery } from "../../../../apis/cart/cart";
import { useGetSampleIdsForProjectQuery } from "../../../../apis/projects/project";

export function ShareSamples({ projectId }) {
  const [samples, setSamples] = React.useState();
  const [existing, setExisting] = React.useState();

  const { data: allSamples, isLoading } = useGetCartSamplesQuery();
  const { data: ids, isLoading: idsLoading } = useGetSampleIdsForProjectQuery(
    projectId,
    {
      skip: projectId === undefined,
    }
  );

  React.useEffect(() => {
    const updatedSamples = [];
    const updateExisting = [];
    if (!isLoading && !idsLoading) {
      allSamples.unlocked.forEach((sample) => {
        // Find out if sample is in the selected project
        if (ids.includes(Number(sample.identifier))) {
          updateExisting.push(sample);
        } else {
          updatedSamples.push(sample);
        }
      });
    }
    setSamples(updatedSamples);
    setExisting(updateExisting);
  }, [allSamples, ids, idsLoading, isLoading]);

  return (
    <div>
      <Typography.Title level={4}>Available Samples</Typography.Title>
      <List
        loading={isLoading}
        bordered
        dataSource={samples}
        renderItem={(sample) => (
          <List.Item
            style={{
              backgroundColor: sample.exists ? "orange" : "transparent",
            }}
          >
            {sample.label}
          </List.Item>
        )}
      />
    </div>
  );
}
