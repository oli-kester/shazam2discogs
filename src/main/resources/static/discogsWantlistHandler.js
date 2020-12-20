const STOP_ADDING_URL = 'stopAdding'
const cancelBtn = document.getElementById('cancelBtn')
const addBtn = document.getElementById('addBtn')
const resultsBtn = document.getElementById('resultsBtn')
const progressBar = document.getElementById('searchProgress')
const progressText = document.getElementById('progressText')

let searchProgressWorker
let adding = false

function resetGui() {
  addBtn.value = 'Add to Discogs Wantlist'
  addBtn.disabled = false
  adding = false
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
document.getElementById('resultsForm').addEventListener('submit', (event) => {
  event.preventDefault() // stop the page refreshing straight away. 
  adding = true
  addBtn.value = 'Please Wait...'
  addBtn.disabled = true
  cancelBtn.hidden = false

  // add worker to keep progress bar updated
  if (window.Worker) {
    searchProgressWorker = new Worker('searchProgressUpdater.js');
    searchProgressWorker.onmessage = (event) => {
      setProgressBar(event.data)

      if (event.data == 100) { //actioned when search completes
        resultsBtn.hidden = false
        addBtn.value = 'Done!'
        cancelBtn.hidden = true
        adding = false
        searchProgressWorker.terminate()
      }
    }
  }

  // when the user tries to exit our page, send a request to 
  // stop the search on the server & re-enable the search button
  window.addEventListener('beforeunload', (event) => {
    if (adding) {
      windowUnloadCanceller(event, STOP_ADDING_URL)
      searchProgressWorker.terminate()
      resetGui()
    }
  })

  // send request to S2D API
  const xhttp = new XMLHttpRequest()
  // pull checkboxes from the form
  const releaseSelectors = Array.from(document.getElementsByClassName('tag-select'))
  const queryString = releaseSelectors.reduce(function (accum, elem) {
    return accum += `${elem.name}=${elem.checked}&`
  }, '/addToDiscogs?'
  )
  xhttp.open('POST', queryString)
  xhttp.send()
})

//handle cancel button presses
cancelBtn.addEventListener('click', () => {
  serverRequestCanceller(STOP_ADDING_URL)
  searchProgressWorker.terminate()
  resetGui()
})