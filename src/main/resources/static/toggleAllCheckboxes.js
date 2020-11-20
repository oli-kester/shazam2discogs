function toggle(source) {
  const checkboxes = document.getElementsByClassName('tag-select')
  for (const checkbox of checkboxes) {
    checkbox.checked = source.target.checked
  }
}

document.getElementById('toggleAll').onclick = toggle