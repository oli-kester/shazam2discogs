const STOP_SEARCH_URL = 'stopSearch'
let searching = false

// handle search button presses
document.getElementById("searchForm").addEventListener('submit', (event) => {
  event.preventDefault() // stop the page refreshing straight away. 
  searching = true
  const searchBtn = document.getElementById('searchBtn')
  searchBtn.value = 'Please Wait...'
  searchBtn.disabled = true
	
  // add worker to keep progress bar updated
  let searchProgressWorker
  if (window.Worker) {
    searchProgressWorker = new Worker('searchProgressUpdater.js');
    searchProgressWorker.onmessage = (event) => {
      const progressBar = document.getElementById('searchProgress')
      progressBar.setAttribute('aria-valuenow', event.data)
      progressBar.setAttribute('style', `width: ${event.data}%`)
    }
  }

  // when the user tries to exit our page, send a request to 
  // stop the search on the server & re-enable the search button
  window.addEventListener("beforeunload", (event) => {
	if (searching) {
		windowUnloadCanceller(event, STOP_SEARCH_URL)
	    searchProgressWorker.terminate()
	    searchBtn.value = 'Search'
	    searchBtn.disabled = false
		searching = false
    }
  })

  // send request to S2D API
  const xhttp = new XMLHttpRequest()
  xhttp.onreadystatechange = function () {
    if (this.readyState == 4 && this.status == 200) {
      searchProgressWorker.terminate()
      document.open()
      document.write(xhttp.responseText)
      document.close()
    }
  };
  const mediaFormat = document.getElementById('media-type').value
  xhttp.open('GET', `/searchTags?mediaType=${mediaFormat}`)
  xhttp.send()
})