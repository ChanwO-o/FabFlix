/**
 * Handle the data returned by PaymentServlet
 */
function handlePaymentResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle payment response");
    console.log(resultDataJson);

    // If payment succeeds, it will redirect the user to result.html
    if (resultDataJson["status"] === "success") {
        window.location.replace("result.html");
    } else {
        // If payment fails, the web page will display
        // error messages on <div> with id "payment_error"
        console.log("show error message");
        console.log(resultDataJson["message"]);
        $("#payment_error").text(resultDataJson["message"]);
        alert("Wrong information!");
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitPaymentForm(formSubmitEvent) {
    formSubmitEvent.preventDefault();
    $.ajax(
        "api/payment", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: payment_form.serialize(),
            success: handlePaymentResult
        }
    );
}

let payment_form = $("#placeorder_form");
let grandTotalText = $("#grand_total");

// Bind the submit action of the form to a handler function
payment_form.submit(submitPaymentForm);

// show grand total by reading from session cart
$.ajax({
    dataType: "json",
    method: "GET",
    url: "api/payment",
    success: (resultData) => { // Setting callback function to handle data returned successfully
        console.log('grand total data: ' + resultData);
        let grandTotalHTML = "<p>" + "Total: $" + resultData['grand_total'] + "</p>"; // display total price
        grandTotalText.append(grandTotalHTML);
    }
});
