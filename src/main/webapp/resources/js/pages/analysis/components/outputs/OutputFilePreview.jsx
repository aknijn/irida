/*
 * This file renders the OutputFilePreview component for analyses
 */

import React, { Suspense, useContext, useEffect } from "react";
import { Dropdown, Menu, Tabs } from "antd";
import { AnalysisContext } from "../../../../contexts/AnalysisContext";
import { AnalysisOutputsContext } from "../../../../contexts/AnalysisOutputsContext";

import { TabPaneContent } from "../../../../components/tabs/TabPaneContent";
import { ContentLoading } from "../../../../components/loader/ContentLoading";
import { AnalysisTabularPreview } from "../AnalysisTabularPreview";
import { WarningAlert } from "../../../../components/alerts/WarningAlert";
import { downloadFilesAsZip } from "../../../../apis/analysis/analysis";
import { downloadIndividualOutputFile } from "../../../../apis/analyses/analyses";
import { IconFile } from "../../../../components/icons/Icons";

const AnalysisTextPreview = React.lazy(() => import("../AnalysisTextPreview"));
const AnalysisJsonPreview = React.lazy(() => import("../AnalysisJsonPreview"));
const AnalysisExcelPreview = React.lazy(() =>
  import("../AnalysisExcelPreview")
);
const AnalysisImagePreview = React.lazy(() =>
  import("../AnalysisImagePreview")
);
const AnalysisHtmlPreview = React.lazy(() => import("../AnalysisHtmlPreview"));

const { TabPane } = Tabs;

export default function OutputFilePreview() {
  const {
    analysisOutputsContext,
    getAnalysisOutputs,
    getPreviewForFileType,
  } = useContext(AnalysisOutputsContext);

  const { analysisIdentifier } = useContext(AnalysisContext);

  useEffect(() => {
    if (analysisOutputsContext.outputs === null) {
      getAnalysisOutputs();
    }
  }, []);

  /*
   * This function gets the menu which is displayed when a user clicks the `...`
   * next to the Download All Files button. This menu has individual download
   * links for each output file generated by the analysis.
   */
  function downloadFilesMenu() {
    let fileNames = [];
    for (const output of analysisOutputsContext.outputs) {
      fileNames.push(<Menu.Item key={output.id}>{output.filename}</Menu.Item>);
    }
    return (
      <Menu
        className="t-download-individual-files-menu"
        onClick={(e) => downloadIndividualOutputFile(analysisIdentifier, e.key)}
      >
        {fileNames}
      </Menu>
    );
  }

  /*
   * This function displays the output preview for excel files
   * generated by the analysis.
   */
  function excelOutputPreview() {
    let excelOutput = [];

    for (const output of analysisOutputsContext.outputs) {
      if (!output.hasOwnProperty("fileExt") || !output.hasOwnProperty("id")) {
        continue;
      }

      // If the output file ext is not json, tabular, pdf. zip, then we create a preview
      // for it and add it to the textOutput array
      if (getPreviewForFileType(output.fileExt, "excel")) {
        excelOutput.unshift(
          <AnalysisExcelPreview output={output} key={output.filename} />
        );
      }
    }
    return excelOutput;
  }

  /*
   * This function displays the output preview for html files
   * generated by the analysis.
   */
  function htmlOutputPreview() {
    let htmlOutput = [];

    for (const output of analysisOutputsContext.outputs) {
      if (!output.hasOwnProperty("fileExt") || !output.hasOwnProperty("id")) {
        continue;
      }

      // If the output file ext is an html file then we create a preview for it
      if (getPreviewForFileType(output.fileExt, "html")) {
        htmlOutput.unshift(
          <AnalysisHtmlPreview output={output} key={output.filename} />
        );
      }
    }
    return htmlOutput;
  }

  /*
   * This function displays the output preview for image files
   * generated by the analysis.
   */
  function imageOutputPreview() {
    let imageOutput = [];

    for (const output of analysisOutputsContext.outputs) {
      if (!output.hasOwnProperty("fileExt") || !output.hasOwnProperty("id")) {
        continue;
      }

      // If the output file ext an image file then we create a preview for it
      if (getPreviewForFileType(output.fileExt, "image")) {
        imageOutput.unshift(
          <AnalysisImagePreview output={output} key={output.filename} />
        );
      }
    }
    return imageOutput;
  }

  /*
   * This function displays the json output preview for json
   * files generated by the analysis.
   */
  function jsonOutputPreview() {
    let jsonOutput = [];

    for (const output of analysisOutputsContext.outputs) {
      if (!output.hasOwnProperty("fileExt") || !output.hasOwnProperty("id")) {
        continue;
      }

      // If the output file ext is json then we create a preview for it
      // and add it to the jsonOutput array
      if (getPreviewForFileType(output.fileExt, "json")) {
        jsonOutput.unshift(
          <AnalysisJsonPreview output={output} key={output.filename} />
        );
      }
    }
    return jsonOutput;
  }

  /*
   * This function displays the tabular output preview for files
   * of tabular type generated by the analysis.
   */
  function tabularOutputPreview() {
    let tabularOutput = [];

    for (const output of analysisOutputsContext.outputs) {
      if (!output.hasOwnProperty("fileExt") || !output.hasOwnProperty("id")) {
        continue;
      }

      // If the output file ext is a tabular type then we create a preview for it
      // and add it to the tabularOutput array
      if (getPreviewForFileType(output.fileExt, "tab")) {
        tabularOutput.unshift(
          <AnalysisTabularPreview output={output} key={output.filename} />
        );
      }
    }
    return tabularOutput;
  }

  /*
   * This function displays the text output preview for files
   * of textual type (.txt, fasta, etc) generated by the analysis.
   */
  function textOutputPreview() {
    let textOutput = [];

    for (const output of analysisOutputsContext.outputs) {
      if (!output.hasOwnProperty("fileExt") || !output.hasOwnProperty("id")) {
        continue;
      }

      // If the output file ext is not json, tabular, pdf. zip, then we create a preview
      // for it and add it to the textOutput array
      if (getPreviewForFileType(output.fileExt, "text")) {
        textOutput.unshift(
          <AnalysisTextPreview output={output} key={output.filename} />
        );
      }
    }
    return textOutput;
  }

  function createDownloadAllButton() {
    return (
      <div style={{ display: "flex", flexDirection: "row-reverse" }}>
        <Dropdown.Button
          className="t-download-all-files-btn"
          onClick={() => downloadFilesAsZip(analysisIdentifier)}
          overlay={downloadFilesMenu()}
        >
          <IconFile />
          {i18n("AnalysisOutputs.downloadAllFiles")}
        </Dropdown.Button>
      </div>
    );
  }

  return analysisOutputsContext.outputs !== null ? (
    <TabPaneContent
      actionButton={
        analysisOutputsContext.outputs.length > 0
          ? createDownloadAllButton()
          : null
      }
      title={i18n("AnalysisOutputs.outputFilePreview")}
      xl={24}
      xxl={24}
    >
      {analysisOutputsContext.outputs.length > 0 ? (
        <div>
          <Tabs defaultActiveKey="1" animated={false}>
            {analysisOutputsContext.fileTypes[0].hasTabularFile ? (
              <TabPane
                tab={i18n("AnalysisOutputs.tabularOutput")}
                key="tab-output"
              >
                {tabularOutputPreview()}
              </TabPane>
            ) : null}

            {analysisOutputsContext.fileTypes[0].hasExcelFile ? (
              <TabPane
                tab={i18n("AnalysisOutputs.excelOutput")}
                key="excel-output"
              >
                <Suspense fallback={<ContentLoading />}>
                  {excelOutputPreview()}
                </Suspense>
              </TabPane>
            ) : null}

            {analysisOutputsContext.fileTypes[0].hasTextFile ? (
              <TabPane
                tab={i18n("AnalysisOutputs.textOutput")}
                key="text-output"
              >
                <Suspense fallback={<ContentLoading />}>
                  {textOutputPreview()}
                </Suspense>
              </TabPane>
            ) : null}

            {analysisOutputsContext.fileTypes[0].hasJsonFile ? (
              <TabPane
                tab={i18n("AnalysisOutputs.jsonOutput")}
                key="json-output"
              >
                <Suspense fallback={<ContentLoading />}>
                  {jsonOutputPreview()}
                </Suspense>
              </TabPane>
            ) : null}

            {analysisOutputsContext.fileTypes[0].hasImageFile ? (
              <TabPane
                tab={i18n("AnalysisOutputs.imageOutput")}
                key="image-output"
              >
                <Suspense fallback={<ContentLoading />}>
                  {imageOutputPreview()}
                </Suspense>
              </TabPane>
            ) : null}

            {analysisOutputsContext.fileTypes[0].hasHtmlFile ? (
              <TabPane
                tab={i18n("AnalysisOutputs.htmlOutput")}
                key="html-output"
              >
                <Suspense fallback={<ContentLoading />}>
                  {htmlOutputPreview()}
                </Suspense>
              </TabPane>
            ) : null}
          </Tabs>
        </div>
      ) : (
        <WarningAlert message={i18n("AnalysisOutputs.noOutputsAvailable")} />
      )}
    </TabPaneContent>
  ) : (
    <ContentLoading />
  );
}
