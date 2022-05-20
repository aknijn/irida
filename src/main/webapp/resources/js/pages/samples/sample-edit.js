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

  if ($("#organism").val() == "Coronavirus") {
    const isolateCombo1 = $('input[name=isolate1]:checked').val()
	if (typeof(isolateCombo1) == "undefined") { isolateCombo1 = "0"; }
    const isolateCombo2 = $('input[name=isolate2]:checked').val()
	if (typeof(isolateCombo2) == "undefined") { isolateCombo2 = "0"; }
    const isolateCombo3 = $('input[name=isolate3]:checked').val()
	if (typeof(isolateCombo3) == "undefined") { isolateCombo3 = "0"; }
    const isolateCombo4 = $('input[name=isolate4]:checked').val()
	if (typeof(isolateCombo4) == "undefined") { isolateCombo4 = "0"; }
    const isolateCombo5 = $('input[name=isolate5]:checked').val()
	if (typeof(isolateCombo5) == "undefined") { isolateCombo5 = "0"; }
    const isolateCombo6 = $('input[name=isolate6]:checked').val()
	if (typeof(isolateCombo6) == "undefined") { isolateCombo6 = "0"; }
    const isolateCombo = isolateCombo1+isolateCombo2+isolateCombo3+isolateCombo4+isolateCombo5+isolateCombo6;
    $("#isolate").val(isolateCombo);
  }
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
