import { Table, Tag } from "antd";
import React from "react";
import { useDispatch, useSelector } from "react-redux";
import {
  getMetadataRestrictions,
  useGetMetadataFieldsForProjectQuery,
} from "../../../apis/metadata/field";
import {
  compareRestrictionLevels,
  getColourForRestriction,
} from "../../../utilities/restriction-utilities";
import { setMetadataRestrictions } from "./shareSlice";
import { TargetMetadataRestriction } from "./TargetMetadataRestriction";

/**
 * React component to display metadata restrictions.
 * @returns {JSX.Element}
 * @constructor
 */
export function ShareMetadata() {
  const dispatch = useDispatch();
  /**
   * Available restrictions for metadata fields
   */
  const [restrictions, setRestrictions] = React.useState([]);

  const { currentProject, projectId, metadataRestrictions } = useSelector(
    (state) => state.shareReducer
  );

  /**
   * Get the fields for the current project.  The restrictions from these fields
   * will act as a base for the restriction level when the fields are shared.
   */
  const { data: sourceFields } = useGetMetadataFieldsForProjectQuery(
    currentProject
  );

  /**
   * Target project metadata fields. Needed to determine, which fields will be
   * on both projects so that the new restriction for a field should start at
   * the highest level of restriction.
   */
  const { data: targetExistingFields } = useGetMetadataFieldsForProjectQuery(
    projectId,
    {
      skip: !projectId,
    }
  );

  React.useEffect(() => {
    if (sourceFields && targetExistingFields) {
      const existing = targetExistingFields.reduce(
        (fields, field) => ({
          ...fields,
          [field.fieldKey]: field,
        }),
        {}
      );
      const fields = sourceFields.map((field) => {
        const f = { ...field, current: field.restriction };
        delete f.initial;
        if (existing[f.fieldKey]) {
          /*
          If field exists in target project
           */
          f.target = existing[f.fieldKey].restriction;
          f.difference = compareRestrictionLevels(f.restriction, f.target);
          f.restriction = f.difference < 0 ? f.target : f.current;
        }
        return f;
      });
      dispatch(setMetadataRestrictions(fields));
    } else if (sourceFields) {
      // Allow user to see what the restrictions are on the source fields
      dispatch(setMetadataRestrictions(sourceFields));
    }
  }, [dispatch, sourceFields, targetExistingFields]);

  /**
   * On load, get metadata restrictions that are possible for a project.
   * These are formatted for Select inputs ({label, value}).
   */
  React.useEffect(() => {
    getMetadataRestrictions().then(setRestrictions);
  }, []);

  const columns = [
    {
      title: "Field",
      key: "label",
      dataIndex: "label",
    },
    {
      title: "Current Restriction",
      key: "current",
      dataIndex: "current",
      render(text, item, index) {
        const field = restrictions.find(
          (r) => r.value === sourceFields[index].restriction
        );
        if (field) {
          return (
            <Tag color={getColourForRestriction(field.value)}>
              {field.label}
            </Tag>
          );
        }
        return text;
      },
    },
    {
      title: "Target Restriction",
      key: "target",
      dataIndex: "restriction",
      render(currentRestriction, item) {
        if (targetExistingFields !== undefined) {
          return (
            <TargetMetadataRestriction
              field={item}
              restrictions={restrictions}
            />
          );
        }
        return "---";
      },
    },
    {
      title: "",
      key: "new",
      dataIndex: "fieldKey",
      width: 100,
      render(restriction, item) {
        if (targetExistingFields === undefined || item.target) return undefined;
        return <Tag>NEW</Tag>;
      },
    },
  ];

  return (
    <Table
      columns={columns}
      dataSource={metadataRestrictions}
      scroll={{ y: 600 }}
      pagination={false}
    />
  );
}
