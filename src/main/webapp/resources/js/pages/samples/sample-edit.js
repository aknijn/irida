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
    let isolateCombo1 = "0";
	if (typeof($('input[name=isolate1]:checked').val()) != "undefined") { isolateCombo1 = $('input[name=isolate1]:checked').val(); }
    let isolateCombo2 = "0";
	if (typeof($('input[name=isolate2]:checked').val()) != "undefined") { isolateCombo2 = $('input[name=isolate2]:checked').val(); }
    let isolateCombo3 = "0";
	if (typeof($('input[name=isolate3]:checked').val()) != "undefined") { isolateCombo3 = $('input[name=isolate3]:checked').val(); }
    let isolateCombo4 = "0";
	if (typeof($('input[name=isolate4]:checked').val()) != "undefined") { isolateCombo4 = $('input[name=isolate4]:checked').val(); }
    let isolateCombo5 = "0";
	if (typeof($('input[name=isolate5]:checked').val()) != "undefined") { isolateCombo5 = $('input[name=isolate5]:checked').val(); }
    let isolateCombo6 = "0";
	if (typeof($('input[name=isolate6]:checked').val()) != "undefined") { isolateCombo6 = $('input[name=isolate6]:checked').val(); }
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
