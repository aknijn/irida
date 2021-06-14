import { Button, Modal, Skeleton, Typography } from "antd";
import React from "react";
import { Provider } from "react-redux";
import { useGetSampleDetailsQuery } from "../../apis/samples/sample";
import { SampleDetails } from "./components/SampleDetails";
import store from "./store";

const { Text } = Typography;

/**
 * React component to render details (metadata and files) for a sample.
 * @param sampleId - identifier for a sample
 * @param removeSample - function to remove the sample from the cart.
 * @param children
 * @returns {JSX.Element}
 * @constructor
 */
function SampleDetailModal({
  sampleId,
  removeSample = Function.prototype,
  children,
}) {
  console.log(sampleId);
  const { data: details, isLoading: loading } = useGetSampleDetailsQuery(
    sampleId
  );
  const [visible, setVisible] = React.useState(false);

  const removeSampleFromCart = () => {
    removeSample({ projectId: details.projectId, sampleId });
  };

  return (
    <>
      {React.cloneElement(children, {
        onClick: () => setVisible(true),
      })}
      {visible ? (
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
                  <span className="t-sample-details-name">
                    {details.sample.sampleName}
                  </span>
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
      ) : null}
    </>
  );
}

export const SampleDetailViewer = ({ children, sampleId }) => (
  <Provider store={store}>
    <SampleDetailModal sampleId={sampleId}>{children}</SampleDetailModal>
  </Provider>
);
