// Saves options to chrome.storage
function save_options() {
  var interval = document.getElementById('interval').value;

  chrome.storage.sync.set({
    "fetch_interval" : interval,
  }, function() {
    // Update status to let user know options were saved.
    var status = document.getElementById('status');
    status.textContent = 'Options saved.';
    setTimeout(function() {
      status.textContent = '';
    }, 750);
  });
}


//document.addEventListener('DOMContentLoaded', restore_options);
document.getElementById('save').addEventListener('click',
                                                 save_options);
