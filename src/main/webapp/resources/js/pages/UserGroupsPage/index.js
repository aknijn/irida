import React, { lazy, Suspense } from "react";
import { render } from "react-dom";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import { getUserGroupRoles } from "../../apis/users/groups";
import { ContentLoading } from "../../components/loader";
import { RolesProvider } from "../../contexts/roles-context";
import { UserGroupsProvider } from "../../contexts/UserGroupsContext";
import { setBaseUrl } from "../../utilities/url-utilities";

/*
WEBPACK PUBLIC PATH:
Webpack does not know what the servlet context path is.  To fix this, webpack exposed
the variable `__webpack_public_path__`
See: https://webpack.js.org/guides/public-path/#on-the-fly
 */
__webpack_public_path__ = setBaseUrl(`/dist/`);

const UserGroupsPage = lazy(() => import("./components/UserGroupsPage"));
const UserGroupsDetailsPage = lazy(() =>
  import("./components/UserGroupDetailsPage")
);

/*
WEBPACK PUBLIC PATH:
Webpack does not know what the servlet context path is.  To fix this, webpack exposed
the variable `__webpack_public_path__`
See: https://webpack.js.org/guides/public-path/#on-the-fly
 */
__webpack_public_path__ = setBaseUrl(`dist/`);

/**
 * React component to display pages related to User Groups.  This is a base page
 * for both listing of user groups and user group details.
 * @returns {*}
 * @constructor
 */
export function UserGroups() {
  const DEFAULT_URL = setBaseUrl("/groups");

  return (
    <Suspense
      fallback={
        <div
          style={{
            height: "100%",
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
          }}
        >
          <ContentLoading message={i18n("UserGroupsPage.loading")} />
        </div>
      }
    >
      <UserGroupsProvider>
        <RolesProvider getRolesFn={getUserGroupRoles}>
          <Routes style={{ height: "100%" }}>
            <Route
              path={DEFAULT_URL}
              element={<UserGroupsPage baseUrl={DEFAULT_URL} />}
            />
            <Route
              path={`${DEFAULT_URL}/:id`}
              element={<UserGroupsDetailsPage baseUrl={DEFAULT_URL} />}
            />
          </Routes>
        </RolesProvider>
      </UserGroupsProvider>
    </Suspense>
  );
}

render(
  <BrowserRouter>
    <UserGroups />
  </BrowserRouter>,
  document.querySelector("#groups-root")
);
