function serverRequestCanceller(requestPath) {
  const xhttp = new XMLHttpRequest()
  xhttp.open('GET', requestPath)
  xhttp.send()
}

function windowUnloadCanceller(event, requestPath) {
  event.preventDefault()
  event.returnValue = ''
  serverRequestCanceller(requestPath)
}