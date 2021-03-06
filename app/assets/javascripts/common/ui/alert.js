define(['common/hasEvents'], function(HasEvents) {

  var Alert = function(alertType, title, message, opt_labels) {
    var self = this,

        labels = opt_labels || {},

        element = jQuery(
          '<div class="clicktrap">' +
            '<div class="alert ' + alertType + '">' +
              '<h1>' + title + '</h1>' +
              '<p>' + message + '</p>' +
              '<p class="buttons">' +
                '<button class="btn ok"></button>' +
                '<button class="btn outline cancel"></button>' +
              '</p>' +
            '</div>' +
          '</div>'),

        btnOK     = element.find('button.ok'),
        btnCancel = element.find('button.cancel'),

        onOK = function() {
          self.fireEvent('ok');
          element.remove();
        },

        onCancel = function() {
          self.fireEvent('cancel');
          element.remove();
        };

    // ERROR and INFO just have an 'OK' button
    if (alertType === Alert.ERROR || alertType === Alert.INFO) {
      btnOK.html(labels.ok || 'OK');
      btnCancel.hide();
    } else {
      btnOK.html(labels.ok || 'YES');
      btnCancel.html(labels.cancel || 'NO');
    }

    btnOK.click(onOK);
    btnCancel.click(onCancel);

    jQuery(document.body).append(element);

    HasEvents.apply(this);
  };
  Alert.prototype = Object.create(HasEvents.prototype);

  /** Type constants **/
  Alert.INFO = 'info';
  Alert.PROMPT = 'info prompt';
  Alert.WARNING = 'warning';
  Alert.ERROR   = 'error';

  return Alert;

});
