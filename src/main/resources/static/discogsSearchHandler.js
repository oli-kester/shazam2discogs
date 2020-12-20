const STOP_SEARCH_URL = 'stopSearch'
const cancelBtn = document.getElementById('cancelBtn')
const searchBtn = document.getElementById('searchBtn')
const resultsBtn = document.getElementById('resultsBtn')
const progressBar = document.getElementById('searchProgress')
const progressText = document.getElementById('progressText')

let searchProgressWorker
let searching = false

function resetGui() {
  searchBtn.value = 'Search'
  searchBtn.disabled = false
  searching = false
  cancelBtn.hidden = true
  resultsBtn.hidden = true
  setProgressBar(0)
}

function setProgressBar(newValue) {
  progressBar.setAttribute('aria-valuenow', newValue)
  progressBar.setAttribute('style', `width: ${newValue}%`)
  progressText.innerText = `${newValue}%`
}

// handle search button presses
document.getElementById('searchForm').addEventListener('submit', (event) => {
  event.preventDefault() // stop the page refreshing straight away. 
  searching = true
  searchBtn.value = 'Please Wait...'
  searchBtn.disabled = true
  cancelBtn.hidden = false

  // add worker to keep progress bar updated
  if (window.Worker) {
    searchProgressWorker = new Worker('searchProgressUpdater.js');
    searchProgressWorker.onmessage = (event) => {
      setProgressBar(event.data)

      if (event.data == 100) { //actioned when search completes
        resultsBtn.hidden = false
        searchBtn.value = 'Done!'
        cancelBtn.hidden = true
        searching = false
        searchProgressWorker.terminate()
      }
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
  const mediaFormat = document.getElementById('media-type').value
  xhttp.open('GET', `/searchTags?mediaType=${mediaFormat}`)
  xhttp.send()
})

//handle cancel button presses
cancelBtn.addEventListener('click', () => {
  serverRequestCanceller(STOP_SEARCH_URL)
  searchProgressWorker.terminate()
  resetGui()
})