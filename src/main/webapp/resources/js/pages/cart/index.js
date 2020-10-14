import React from "react";
import { render } from "react-dom";
import { Col, Row, Space, Steps, Table } from "antd";
import { setBaseUrl } from "../../utilities/url-utilities";
import { SamplesProvider, useSamplesState } from "./samples-context";
import { IconFile } from "../../components/icons/Icons";
import { SPACE_MD } from "../../styles/spacing";

/*
WEBPACK PUBLIC PATH:
Webpack does not know what the servlet context path is.  To fix this, webpack exposed
the variable `__webpack_public_path__`
See: https://webpack.js.org/guides/public-path/#on-the-fly
 */
__webpack_public_path__ = setBaseUrl(`dist/`);

function CartLayout() {
  const {loading, samples} = useSamplesState();

  return (
    <Row style={{width: `100%`, padding: SPACE_MD}} gutter={[16, 16]}>
      <Col span={24}>
        <Space direction="vertical" style={{width: `100%`}}><Steps>
          <Steps.Step title={"FILES"} icon={<IconFile/>}/>
          <Steps.Step title={"FILES"} icon={<IconFile/>}/>
          <Steps.Step title={"FILES"} icon={<IconFile/>}/>
        </Steps>
          {loading ? null : (
            <Table
              showHeader={false}
              pagination={false}
              bordered={false}
              rowKey={sample => sample.id}
              columns={[
                {
                  key: "sample",
                  render(item, sample) {
                    return sample.label;
                  },
                },
              ]}
              size="small"
              dataSource={samples}
              expandable={{
                expandedRowRender(sample) {
                  return sample.pairs[0].label;
                },
              }}
            />
          )}</Space>
      </Col>
    </Row>
  );
}

render(
  <SamplesProvider>
    <CartLayout/>
  </SamplesProvider>,
  document.querySelector("#root")
);
