const STOP_SEARCH_URL = 'stopSearch'
const cancelBtn = document.getElementById('cancelBtn')
const searchBtn = document.getElementById('searchBtn')
const progressBar = document.getElementById('searchProgress')

let searching = false

function resetGui() {
  searchBtn.value = 'Search'
  searchBtn.disabled = false
  searching = false
  cancelBtn.hidden = true
}

// handle search button presses
document.getElementById('searchForm').addEventListener('submit', (event) => {
  event.preventDefault() // stop the page refreshing straight away. 
  searching = true
  searchBtn.value = 'Please Wait...'
  searchBtn.disabled = true
  cancelBtn.hidden = false

  // add worker to keep progress bar updated
  let searchProgressWorker
  if (window.Worker) {
    searchProgressWorker = new Worker('searchProgressUpdater.js');
    searchProgressWorker.onmessage = (event) => {
      progressBar.setAttribute('aria-valuenow', event.data)
      progressBar.setAttribute('style', `width: ${event.data}%`)
    }
  }

  // when the user tries to exit our page, send a request to 
  // stop the search on the server & re-enable the search button
  window.addEventListener('beforeunload', (event) => {
    if (searching) {
      windowUnloadCanceller(event, STOP_SEARCH_URL)
      searchProgressWorker.terminate()
      resetGui()
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

//handle cancel button presses
cancelBtn.addEventListener('click', () => {
  serverRequestCanceller(STOP_SEARCH_URL)
  resetGui()
})