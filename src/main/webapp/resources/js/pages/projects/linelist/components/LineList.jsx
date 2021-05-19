import React from "react";

import PropTypes from "prop-types";
import { Loader } from "./Loader";
import { LineListLayoutComponent } from "./LineList/LineListLayoutComponent";
import { ErrorAlert } from "../../../../components/alerts/ErrorAlert";

/**
 * Container class for the higher level states of the page:
 * 1. Loading
 * 2. Table
 * 3. Loading error.
 */
export default function LineList({ projectId }) {
  console.log(projectId);
  // if (initializing) {
  //   return <Loader />;
  // } else if (props.error) {
  //   return (
  //     <ErrorAlert message={i18n("linelist.error.message", project.name)} />
  //   );
  // }

  return <div>LOADING</div>;
  // return <LineListLayoutComponent {...props} />;
}
