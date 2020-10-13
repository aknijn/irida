import React from "react";
import { render } from "react-dom";
import { Row } from "antd";
import { setBaseUrl } from "../../utilities/url-utilities";
import { SamplesProvider } from "./samples-context";

/*
WEBPACK PUBLIC PATH:
Webpack does not know what the servlet context path is.  To fix this, webpack exposed
the variable `__webpack_public_path__`
See: https://webpack.js.org/guides/public-path/#on-the-fly
 */
__webpack_public_path__ = setBaseUrl(`dist/`);

function CartLayout() {
  return (
    <Row style={{ backgroundColor: "orange", width: `100%` }}>
      <SamplesProvider>
        <div>FOOBAR</div>
      </SamplesProvider>
    </Row>
  );
}

render(<CartLayout />, document.querySelector("#root"));
