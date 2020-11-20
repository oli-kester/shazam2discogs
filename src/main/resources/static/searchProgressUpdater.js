// fetches progress updates every second
function updateProgressBar() {
  const xhttp = new XMLHttpRequest()
  xhttp.onreadystatechange = function () {
    if (this.readyState == 4 && this.status == 200) {
      postMessage(this.responseText)
    }
  }
  xhttp.open('GET', 'getProgress', false)
  xhttp.send()
  setTimeout(updateProgressBar, 3000)
}

updateProgressBar()