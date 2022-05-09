import $ from "jquery";
import "../../../css/pages/sample-edit.css";

/**
 * Serialize metadata to json for submission
 */
$("#edit-form").submit(function () {
  const metadata = {};
  $("#other-metadata")
    .find(".metadata-entry")
    .each(function () {
      const entry = $(this);
      const key = entry.find(".metadata-key").val();
      const value = entry.find(".metadata-value").val();
      metadata[key] = { value: value, type: "text" };
    });

  // paste the json into a hidden text field for submission
  if (Object.keys(metadata).length > 0) {
    $("#metadata").val(JSON.stringify(metadata));
  }

  var isolated = '000000';
  for (var option of $("#isolateCombo").options)
  {
    if (option.selected) {
      isolated |= option.value;
    }
  }
  isolated = '000000' + isolated;
  $("#isolate").val(isolated.slice(-6));
});

/**
 * Add a metadata term from the template
 */
$("#add-metadata").on("click", function () {
  const newMetadata = $("#metadata-template").clone(true);
  newMetadata.removeAttr("id");
  $("#metadata-fields").append(newMetadata);
});

/**
 * Remove a metadata term
 */
$(".delete-metadata").on("click", function () {
  $(this).closest(".metadata-entry").remove();
});
