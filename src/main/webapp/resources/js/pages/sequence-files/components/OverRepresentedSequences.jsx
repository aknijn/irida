/*
 * This file renders the OverRepresentedSequences component.
 */

import React, { useEffect, useState } from "react";
import { Layout, Table, Typography } from "antd";
import { SPACE_MD } from "../../../styles/spacing";
import { grey1 } from "../../../styles/colors";
import { TabPaneContent } from "../../../components/tabs/TabPaneContent";
import { getOverRepresentedSequences } from "../../../apis/files/sequence-files";
import {
  seqObjId,
  seqFileId
} from "../fastqc-constants";

export default function OverRepresentedSequences() {
  const [fastQC, setFastQC] = useState({});
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getOverRepresentedSequences(seqObjId, seqFileId).then(analysisFastQC => {
      console.log(analysisFastQC);
      setFastQC(analysisFastQC);
      setLoading(false);
    });
  }, []);

  const columns = [
    {
      title: "Sequence",
      key: "sequence",
      dataIndex: "sequence",
    },
    {
      title: "Percentage",
      key: "percentage",
      dataIndex: "percentage",
    },
    {
      title: "Count",
      key: "overrepresentedSequenceCount",
      dataIndex: "overrepresentedSequenceCount",
    },
    {
      title: "Possible Source",
      key: "possibleSource",
      dataIndex: "possibleSource",
    },

  ]

  return (
    <Layout style={{ paddingLeft: SPACE_MD, backgroundColor: grey1 }}>
      <TabPaneContent title={`Overrepresented Sequences`}>
        <Typography.Paragraph className="text-info">{fastQC.description}</Typography.Paragraph>
        <Table
          bordered
          rowKey="id"
          loading={loading}
          columns={columns}
          dataSource={fastQC.overrepresentedSequences}
        />
      </TabPaneContent>
    </Layout>
  );
}