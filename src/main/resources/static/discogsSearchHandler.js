// handle search button presses
document.getElementById("searchForm").addEventListener('submit', (event) => {
  event.preventDefault() // stop the page refreshing straight away. 

  // add worker to keep progress bar updated
  if (window.Worker) {
    const searchProgressWorker = new Worker('searchProgressUpdater.js');
		searchProgressWorker.onmessage = (event) => {
	      const progressBar = document.getElementById('searchProgress')
	      progressBar.setAttribute('aria-valuenow', event.data)
	      progressBar.setAttribute('style', `width: ${event.data}%`)
	}
  }

  // send request to S2D API
  const xhttp = new XMLHttpRequest()
  xhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
	document.open()
	document.write(xhttp.responseText)
	document.close()
    }
  };
  const mediaFormat = document.getElementById('media-type').value
  xhttp.open('GET', `/searchTags?mediaType=${mediaFormat}`)
  xhttp.send()
})