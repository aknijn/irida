import React, { Component, Suspense } from "react";
import { connect } from "react-redux";

import PropTypes from "prop-types";
import { actions as entryActions } from "../reducers/entries";
import { ExportDropDown } from "./Export/ExportDropdown";
import { AddSamplesToCartButton } from "./AddToCartButton/AddSamplesToCart";
import { Button, Form, Input, Popover } from "antd";
import {
  IconCloudUpload,
  IconQuestion,
} from "../../../../components/icons/Icons";

const LineListTour = React.lazy(() => import("./Tour/LineListTour"));

const { Search } = Input;

const { urls } = window.PAGE;

export default function () {
  const [tourOpen, setTourOpen] = React.useState(false);
  const [showTourPopover, setShowTourPopover] = React.useState(false);

  React.useEffect(() => {
    if (typeof window.localStorage === "object") {
      if (!window.localStorage.getItem("linelist-tour")) {
        window.localStorage.setItem("linelist-tour", "complete");
        setShowTourPopover(true);
        const timer = window.setTimeout(() => {
          showTourPopover(false);
        }, 10000);
        return () => window.clearTimeout(timer);
      }
    }
  }, []);

  const openTour = () => {
    this.props.scrollTableToTop();
    this.setState({ tourOpen: true, showTourPopover: false });
  };

  const closePopover = () => {
    window.clearTimeout(this.timer);
    setShowTourPopover(false);
  };

  const closeTour = () => setTourOpen(false);

  return (
    <div className="toolbar">
      <div className="toolbar-group">
        <Form layout="inline">
          <Form.Item>
            <ExportDropDown
              csv={this.props.exportCSV}
              excel={this.props.exportXLSX}
            />
          </Form.Item>
          <Form.Item>
            <AddSamplesToCartButton
              selectedCount={this.props.selectedCount}
              addSamplesToCart={this.props.addSamplesToCart}
            />
          </Form.Item>
        </Form>
      </div>
      <div className="toolbar-group">
        <Form layout="inline">
          {window.project.canManage ? (
            <Form.Item>
              <Button
                className="t-import-metadata-btn"
                href={urls.import}
                tour="tour-import"
              >
                <IconCloudUpload />
                {i18n("linelist.importBtn.text")}
              </Button>
            </Form.Item>
          ) : null}
          <Form.Item>
            <Search
              tour="tour-search"
              onKeyUp={(e) => this.props.updateFilter(e.target.value)}
              id="js-table-filter"
              className="table-filter t-table-filter"
              style={{
                width: 200,
              }}
            />
          </Form.Item>
          <Form.Item>
            <Suspense fallback={<span />}>
              {this.state.tourOpen ? (
                <LineListTour
                  isOpen={this.state.tourOpen}
                  closeTour={this.closeTour}
                />
              ) : null}
            </Suspense>
            <Popover
              content={
                <strong
                  style={{
                    borderBottom: "2px solid orange",
                    cursor: "pointer",
                  }}
                  onClick={this.closePopover}
                >
                  {i18n("linelist.tour.popover")}
                </strong>
              }
              visible={this.state.showTourPopover}
              placement="topLeft"
              arrowPointAtCenter
            >
              <Button
                title={i18n("linelist.tour.title")}
                className="js-tour-button t-tour-button tour-button"
                shape="circle"
                onClick={this.openTour}
              >
                <IconQuestion />
              </Button>
            </Popover>
          </Form.Item>
        </Form>
      </div>
    </div>
  );
}
