import React from "react";
import { Button, Layout, Menu } from "antd";
import styled from "styled-components";
import { grey1, grey10, grey6, grey8 } from "../../styles/colors";
import { SPACE_MD } from "../../styles/spacing";
import { theme } from "../../utilities/theme-utilities";
import { setBaseUrl } from "../../utilities/url-utilities";
import { IconUser } from "../icons/Icons";
import { AnnouncementsSubMenu } from "./main-navigation/components/AnnouncementsSubMenu";
import { CartLink } from "./main-navigation/components/CartLink";
import { GlobalSearch } from "./main-navigation/components/GlobalSearch";
import "./main-navigation/style.css";
const { Header } = Layout;

const isAdmin = window.TL._USER.systemRole === "ROLE_ADMIN";
const isManager = isAdmin || window.TL._USER.systemRole === "ROLE_MANAGER";

const StyledHeader = styled(Header)`
  background-color: ${theme === "dark" ? grey10 : grey1};
  position: fixed;
  z-index: 1;
  width: 100%;
  display: flex;

  .ant-menu-item-active {
    background-color: transparent !important;
  }

  .anticon {
    font-size: 20px;
    color: ${theme === "dark" ? grey1 : grey10};
  }
`;

export function MainNavigation() {
  return (
    <StyledHeader>
      <a href={setBaseUrl("/")}>
        <img
          style={{ height: 28, width: 129 }}
          src={setBaseUrl(`/resources/img/irida_logo_${theme}.svg`)}
          alt={i18n("global.title")}
        />
      </a>
      <Menu
        theme={theme}
        mode="horizontal"
        style={{
          display: "inline-block",
          minWidth: 400,
        }}
        overflowedIndicator={<span>MENU</span>}
      >
        <Menu.SubMenu key="projects" title={i18n("nav.main.projects")}>
          <Menu.Item key="project:list">
            {i18n("nav.main.project-list")}
          </Menu.Item>
          {isAdmin && (
            <Menu.Item key="project:all">
              {i18n("nav.main.project-list-all")}
            </Menu.Item>
          )}
          <Menu.Divider />
          <Menu.Item key="project:sync">
            <a href={setBaseUrl("/projects/synchronize")}>
              {i18n("nav.main.project-sync")}
            </a>
          </Menu.Item>
        </Menu.SubMenu>

        <Menu.SubMenu key="analysis" title={i18n("nav.main.analysis")}>
          <Menu.Item key="analyses:user">
            <a href={setBaseUrl(`/analysis`)}>
              {i18n("nav.main.analysis-admin-user")}
            </a>
          </Menu.Item>
          {isAdmin && (
            <Menu.Item key="analyses:all">
              <a href={setBaseUrl("/analysis/all")}>
                {i18n("nav.main.analysis-admin-all")}
              </a>
            </Menu.Item>
          )}
          <Menu.Divider />
          <Menu.Item key="analyses:output">
            <a href={setBaseUrl("/analysis/user/analysis-outputs")}>
              {i18n("Analysis.outputFiles")}
            </a>
          </Menu.Item>
        </Menu.SubMenu>

        {!isAdmin && isManager && (
          <Menu.SubMenu key="users" title={i18n("nav.main.users")}>
            <Menu.Item key="user:users">
              <a href={setBaseUrl("/users")}>{i18n("nav.main.users-list")}</a>
            </Menu.Item>
            <Menu.Item key="user:groups">
              <a href={setBaseUrl("/groups")}>{i18n("nav.main.groups-list")}</a>
            </Menu.Item>
          </Menu.SubMenu>
        )}
        {!isAdmin && (
          <Menu.Item key="remote_api">
            <Button type="link" href={setBaseUrl("/remote_api")}>
              {i18n("nav.main.remoteapis")}
            </Button>
          </Menu.Item>
        )}
      </Menu>
      <div style={{ content: "", flexGrow: 1 }} />
      <GlobalSearch />
      {isAdmin && (
        <div style={{ padding: `0 ${SPACE_MD}` }}>
          <Button
            type="primary"
            className="t-admin-panel-btn"
            href={setBaseUrl("/admin")}
          >
            {i18n("MainNavigation.admin").toUpperCase()}
          </Button>
        </div>
      )}
      <CartLink />
      <AnnouncementsSubMenu />
      <Menu theme={theme} mode="horizontal" defaultSelectedKeys={[""]}>
        <Menu.SubMenu
          key="account-dropdown-link"
          icon={<IconUser />}
          title={window.TL._USER.username}
        >
          <Menu.Item key="account">
            <a href={setBaseUrl(`/users/current`)}>
              {i18n("nav.main.account")}
            </a>
          </Menu.Item>
          <Menu.SubMenu key="help" title={i18n("nav.main.help")}>
            <Menu.Item key="userguide">
              <a
                href="https://irida.corefacility.ca/documentation/user/user"
                target="_blank"
                rel="noreferrer"
              >
                {i18n("nav.main.userguide")}
              </a>
            </Menu.Item>
            {isAdmin && (
              <Menu.Item key="adminguide">
                <a href="https://irida.corefacility.ca/documentation/user/administrator">
                  {i18n("nav.main.adminguide")}
                </a>
              </Menu.Item>
            )}
            <Menu.Divider />
            <Menu.Item key="website">
              <a
                href="http://www.irida.ca"
                target="_blank"
                rel="noopener noreferrer"
              >
                {i18n("generic.irida.website")}
              </a>
            </Menu.Item>
            <Menu.Divider />
            <Menu.Item key="help:version" disabled>
              {i18n("irida.version")}
            </Menu.Item>
          </Menu.SubMenu>
          <Menu.Item key="logout">
            <a href={setBaseUrl("/logout")}>{i18n("nav.main.logout")}</a>
          </Menu.Item>
        </Menu.SubMenu>
      </Menu>
    </StyledHeader>
  );
}
