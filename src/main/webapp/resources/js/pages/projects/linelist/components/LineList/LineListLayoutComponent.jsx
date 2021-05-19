/**
 * @file Component for the overall layout of the line list table, and has the
 * responsibility of delegating function to the different areas of the table.
 */

import React from "react";
import { Layout } from "antd";
import { Table } from "../Table";
import Toolbar from "../Toolbar";
import { InfoBar } from "../InfoBar";
import TableControlPanel from "../TableControlPanel/TableControlPanel";
import { AgGridLayout } from "../../../../../components/Tables/AgGridLayout";

const { Sider, Content } = Layout;

export function LineListLayoutComponent({ projectId }) {
  const [collapsed, setCollapsed] = React.useState(true);
  const [height, setHeight] = React.useState(800);
  const linelistRef = React.createRef();

  React.useEffect(() => {
    updateHeight();
    window.addEventListener("resize", updateHeight);
    return () => window.removeEventListener("resize", updateHeight);
  }, []);

  /**
   * Multiple components need to be updated when the window height changes.  This determines
   * the new height required and sets it into the state.
   */
  function updateHeight() {
    if (window.innerHeight > 600) {
      const BOTTOM_BUFFER = 90;
      /*
      Determine the height the linelist should be based on the size of the window,
      and a small buffer at the bottom of the page.
       */
      const height =
        window.innerHeight -
        linelistRef.current.getBoundingClientRect().top -
        BOTTOM_BUFFER;
      setHeight(height);
    } else {
      // Just preventing table from getting overly small!
      setHeight(300);
    }
  }
  //
  // /**
  //  * Toggle the open state of the tool panel.
  //  */
  // const toggleTableControlPanel = () => setCollapsed(!collapsed);
  //
  // /**
  //  * Export table to a csv file
  //  */
  // const exportCSV = () => tableRef.exportCSV();
  //
  // /**
  //  * Export table to an excel file
  //  */
  // const exportXLSX = () => tableRef.exportXLSX();
  //
  // /**
  //  * Update the state of the filter
  //  * @param count
  //  */
  // const updateFilterCount = (count) => {
  //   // setState({ filterCount: count });
  //   console.log({ count });
  // };
  //
  // /**
  //  * Scroll the table to the top.
  //  */
  // const scrollTableToTop = () => tableRef.scrollToTop();

  return (
    <div ref={linelistRef}>
      HELLO
      {/*<Toolbar*/}
      {/*  exportCSV={exportCSV}*/}
      {/*  exportXLSX={exportXLSX}*/}
      {/*  addSamplesToCart={addSamplesToCart}*/}
      {/*  selectedCount={props.selectedCount}*/}
      {/*  scrollTableToTop={scrollTableToTop}*/}
      {/*/>*/}
      {/*<AgGridLayout height={state.height}>*/}
      {/*  <Content>*/}
      {/*    <Table*/}
      {/*      ref={(tableReference) => (tableRef = tableReference)}*/}
      {/*      onFilter={updateFilterCount}*/}
      {/*    />*/}
      {/*  </Content>*/}
      {/*  <Sider*/}
      {/*    className="tool-panel-slider"*/}
      {/*    trigger={null}*/}
      {/*    collapsedWidth="42"*/}
      {/*    width="300"*/}
      {/*    collapsible*/}
      {/*    collapsed={state.collapsed}*/}
      {/*  >*/}
      {/*    <TableControlPanel*/}
      {/*      height={state.height}*/}
      {/*      saved={props.saved}*/}
      {/*      saveTemplate={props.saveTemplate}*/}
      {/*      useTemplate={props.useTemplate}*/}
      {/*      togglePanel={toggleTableControlPanel}*/}
      {/*      templates={props.templates}*/}
      {/*      current={props.current}*/}
      {/*      templateModified={props.templateModified}*/}
      {/*    />*/}
      {/*  </Sider>*/}
      {/*</AgGridLayout>*/}
      {/*<InfoBar*/}
      {/*  selectedCount={props.selectedCount}*/}
      {/*  filterCount={*/}
      {/*    state.filterCount*/}
      {/*      ? state.filterCount*/}
      {/*      : props.entries*/}
      {/*      ? props.entries.length*/}
      {/*      : 0*/}
      {/*  }*/}
      {/*  totalSamples={props.entries ? props.entries.length : 0}*/}
      {/*/>*/}
    </div>
  );
}
