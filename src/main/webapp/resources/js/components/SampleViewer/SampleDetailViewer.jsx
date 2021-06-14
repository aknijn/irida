import { Button, Modal, Skeleton, Typography } from "antd";
import React from "react";
import { Provider } from "react-redux";
import { useGetSampleDetailsQuery } from "../../apis/samples/sample";
import { SampleDetails } from "./components/SampleDetails";
import store from "./store";

const { Text } = Typography;

/**
 * React component to render details (metadata and files) for a sample.
 * @param sample - identifier for a sample and the name
 * @param removeSample - function to remove the sample from the cart.
 * @param children
 * @returns {JSX.Element}
 * @constructor
 */
function SampleDetailModal({
  sample,
  removeSample = Function.prototype,
  children,
}) {
  console.log(sample.id);
  const { data: details = {}, isLoading: loading } = useGetSampleDetailsQuery(
    sample.id
  );
  const [visible, setVisible] = React.useState(false);

  const removeSampleFromCart = () => {
    removeSample({ projectId: details.projectId, sampleId: sample.id });
  };

  return (
    <>
      {React.cloneElement(children, {
        onClick: () => setVisible(true),
      })}
      <Modal
        className="t-sample-details-modal"
        bodyStyle={{
          padding: 0,
          maxHeight: window.innerHeight - 400,
          overflowY: "auto",
        }}
        title={
          loading ? null : (
            <div
              style={{
                display: "flex",
                justifyContent: "space-between",
                alignItems: "center",
              }}
            >
              <Text strong>
                <span className="t-sample-details-name">{sample.name}</span>
              </Text>
              <Button
                size="small"
                danger
                style={{ marginRight: 30 }}
                onClick={removeSampleFromCart}
              >
                {i18n("SampleDetailsSidebar.removeFromCart")}
              </Button>
            </div>
          )
        }
        visible={visible}
        onCancel={() => setVisible(false)}
        footer={null}
        width={720}
      >
        <div style={{ margin: 24 }}>
          {loading ? (
            <Skeleton active title />
          ) : (
            <SampleDetails details={details} />
          )}
        </div>
      </Modal>
    </>
  );
}

export const SampleDetailViewer = ({ children, sample }) => (
  <Provider store={store}>
    <SampleDetailModal sample={sample}>{children}</SampleDetailModal>
  </Provider>
);
