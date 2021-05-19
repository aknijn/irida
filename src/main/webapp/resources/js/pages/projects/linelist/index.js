import { Router } from "@reach/router";
import React from "react";
import { render } from "react-dom";
import "./linelist.css";
import { Provider } from "react-redux";
import { setBaseUrl } from "../../../utilities/url-utilities";
import LineList from "./components/LineList";
import { LineListLayoutComponent } from "./components/LineList/LineListLayoutComponent";
import store from "./store";

__webpack_public_path__ = setBaseUrl(`dist/`);

// Render the application
render(
  <Provider store={store}>
    <Router>
      <LineListLayoutComponent
        path={setBaseUrl(`/projects/:projectId/linelist`)}
      />
    </Router>
  </Provider>,
  document.querySelector("#root")
);
