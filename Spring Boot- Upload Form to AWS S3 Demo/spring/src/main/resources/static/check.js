
function validateFile() {
    var fileUploadControl = document.getElementById("file");
    var file = fileUploadControl.files[0];
    if (!file) {
        fileUploadControl.value = "";
        return true;
    }
    return false;
}

function validate_form(){
    var day = document.getElementById("day").value
    var month = document.getElementById("month").value
    var year = document.getElementById("year").value
    var name = document.getElementById("company name").value
    var file = document.getElementById("file").value

    var check = day === ""|| month === "" ||  year === ""
     || name  === "" ||  file === "";

    if (check){
        alert("Not Completed");
        return false;
    } else {

        return true;
    }
}