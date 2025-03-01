/*
 * This file is responsible for displaying the
 * tabs required and default renders the
 * FastQCCharts component.
 */

import { Badge, Menu, Skeleton, Space } from "antd";
import React from "react";
import { Link, Outlet, useLocation, useParams } from "react-router-dom";
import { InfoAlert } from "../../../components/alerts";
import { PageWrapper } from "../../../components/page/PageWrapper";
import { blue6 } from "../../../styles/colors";

import { SPACE_XS } from "../../../styles/spacing";
import { FastQCProvider, useFastQCState } from "../fastqc-context";

function FastQCMenu({ current }) {
  const location = useLocation();
  const [key, setKey] = React.useState(current);
  const { loading, fastQC } = useFastQCState();

  let uri = location.pathname;
  if (uri.endsWith(current)) {
    // Strip of the current location as it will mess up the links
    uri = uri.replace(`/${current}`, "");
  }

  return (
    <Menu mode="horizontal" selectedKeys={[key]} className="t-fastQC-nav"
          onClick={e => setKey(e.key)}>
      <Menu.Item key="charts">
        <Link to={`${uri}/charts`}>{i18n("FastQC.charts")}</Link>
      </Menu.Item>
      <Menu.Item key="overrepresented">
        <Link to={`${uri}/overrepresented`}>
          {i18n("FastQC.overrepresentedSequences")}
          <Badge
            count={loading ? "-" : fastQC.overrepresentedSequences.length}
            showZero
            style={{ backgroundColor: blue6, marginLeft: SPACE_XS }}
            className="t-overrepresented-sequences-count"
          />
        </Link>
      </Menu.Item>
      <Menu.Item key="details">
        <Link to={`${uri}/details`}>{i18n("FastQC.details")}</Link>
      </Menu.Item>
    </Menu>
  );
}

function FastQCContent({ children, current, uri }) {
  const { loading, fastQC, file = {} } = useFastQCState();

  return (
    <Skeleton loading={loading} active>
      <PageWrapper title={file.fileName}>
        {fastQC ? (
          <Space direction="vertical" style={{ width: `100%` }}>
            <FastQCMenu current={current} uri={uri} />
            {children}
          </Space>
        ) : (
          <div>
            <InfoAlert
              message={i18n("FastQC.noResults")}
              style={{ marginBottom: SPACE_XS }}
              className="t-fastQC-no-run"
            />
          </div>
        )}
      </PageWrapper>
    </Skeleton>
  );
}

export default function FastQC({ current }) {
  const { sequenceObjectId, fileId } = useParams();
  return (
    <FastQCProvider sequenceObjectId={sequenceObjectId} fileId={fileId}>
      <FastQCContent current={current}>
        <Outlet />
      </FastQCContent>
    </FastQCProvider>
  );
}
