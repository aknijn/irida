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

  const isolateCombo = '000000';
  if ($("#isolate1") != null) {
    isolateCombo = $("#isolate1").val();
    isolateCombo = $('input[name=isolate1]:checked').val();
    isolateCombo = $('input[name=isolate2]:checked').val();
    isolateCombo = $('input[name=isolate1]:checked').val()+$('input[name=isolate2]:checked').val()+$('input[name=isolate3]:checked').val()+$('input[name=isolate4]:checked').val()+$('input[name=isolate5]:checked').val()+$('input[name=isolate6]:checked').val();
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
