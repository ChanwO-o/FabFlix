function handlePaymentResult(resultData) {
    console.log('handlePaymentResult()', resultData);

    let grandTotalHTML = "<p>" + "Total: $" + resultData['grand_total'] + "</p>"; // display total price
    grandTotalText.append(grandTotalHTML);
}

let grandTotalText = $("#grand_total");

$.ajax({
    dataType: "json",
    method: "GET",
    url: "api/payment",
    success: (resultData) => handlePaymentResult(resultData) // Setting callback function to handle data returned successfully
});
