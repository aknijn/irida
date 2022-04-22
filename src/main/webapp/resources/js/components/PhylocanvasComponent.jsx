/*
 * This file renders a phylocanvas react component.
 */

import PropTypes from "prop-types";
import React, { useEffect, useState } from "react";
import PhyloCanvas, { treeTypes } from "phylocanvas";
import _keys from "lodash/keys";

export function PhylocanvasComponent({ data, branch, className, style, treeType }) {
  const [currentTree, setCurrentTree] = useState(null);

  /* Creates the (phylocanvas div, loads the tree data, and sets the tree type)
   * and sets the current tree to this object. If the tree type changes
   * the data is loaded into the tree and the tree type is set to the newly
   * selected shape.
   */
  useEffect(() => {
    let tree =
      currentTree != null
        ? currentTree
        : PhyloCanvas.createTree("phyloCanvasDiv");
    tree.load(data);
    tree.setTreeType(treeType);
	tree.branches[branch].selected = true;
    setCurrentTree(tree);
  }, [treeType]);

  // Renders the phylocanvas tree
  return <div id="phyloCanvasDiv" style={style} className={className} />;
}

PhylocanvasComponent.propTypes = {
  branch: PropTypes.string,
  className: PropTypes.string,
  data: PropTypes.string,
  style: PropTypes.object,
  treeType: PropTypes.oneOf(_keys(treeTypes))
};
