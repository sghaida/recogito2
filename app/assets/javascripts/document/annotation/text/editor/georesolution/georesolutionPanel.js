define([
  'document/annotation/text/editor/georesolution/placeResult',
  'common/helpers/formatting',
  'common/helpers/gazetteerUtils',
  'common/helpers/placeUtils',
  'common/api',
  'common/hasEvents'], function(Result, Formatting, GazetteerUtils, PlaceUtils, API, HasEvents) {

  var GeoresolutionPanel = function() {
    var self = this,

        element = (function() {
            var el = jQuery('<div class="clicktrap">' +
                  '<div class="geores-wrapper">' +
                    '<div class="geores-editor">' +
                      '<div class="geores-editor-header">' +
                        '<div class="geores-search">' +
                          '<input class="search inline" type="text" placeholder="Search for a Place..." />' +
                          '<button class="search icon">&#xf002;</button>' +
                        '</div>' +
                        '<div class="flag-this-place">' +
                          'Can\'t find the right match?' +
                          '<span class="flag">' +
                            '<button class="outline-icon nostyle">&#xe842;</button>' +
                            '<span class="label">Flag this place</span>' +
                          '</span>' +
                        '</div>' +
                        '<button class="nostyle outline-icon cancel">&#xe897;</button>' +
                      '</div>' +
                      '<div class="geores-editor-body">' +
                        '<div class="geores-sidebar">' +
                          '<div class="results-header"></div>' +
                          '<ul class="results-list"></ul>' +
                        '</div>' +
                        '<div class="geores-map">' +
                        '</div>' +
                      '</div>' +
                    '</div>' +
                  '</div>' +
                '</div>');

            el.hide();
            jQuery(document.body).append(el);
            return el;
          })(),

          searchInput = element.find('.search'),

          btnFlag = element.find('.flag'),

          btnCancel = element.find('.cancel'),

          resultsHeader = element.find('.results-header'),

          resultsList = element.find('.results-list'),

          currentBody = false,

          awmc = L.tileLayer('http://a.tiles.mapbox.com/v3/isawnyu.map-knmctlkh/{z}/{x}/{y}.png', {
            attribution: 'Tiles &copy; <a href="http://mapbox.com/" target="_blank">MapBox</a> | ' +
                         'Data &copy; <a href="http://www.openstreetmap.org/" target="_blank">OpenStreetMap</a> and contributors, CC-BY-SA | '+
                         'Tiles and Data &copy; 2013 <a href="http://www.awmc.unc.edu" target="_blank">AWMC</a> ' +
                         '<a href="http://creativecommons.org/licenses/by-nc/3.0/deed.en_US" target="_blank">CC-BY-NC 3.0</a>'
          }),

          map = L.map(element.find('.geores-map')[0], {
            center: new L.LatLng(41.893588, 12.488022),
            zoom: 4,
            zoomControl: false,
            layers: [ awmc ]
          }),

          markerLayer = L.layerGroup().addTo(map),

          openPopup = function(marker, place) {
            var popup = jQuery(
                  '<div class="popup">' +
                    '<div class="popup-header">' +
                      '<h3>' + place.labels.join(', ') + '</h3>' +
                    '</div>' +
                    '<div class="popup-choices">' +
                      '<table></table>' +
                    '</div>' +
                  '</div>');

            jQuery.each(place.is_conflation_of, function(idx, choice) {
              var choiceProps = GazetteerUtils.parseURI(choice.uri),

                  template = jQuery(
                    '<tr data-uri="' + choice.uri + '">' +
                      '<td class="g">' +
                        '<span class="g-shortcode"></span>' +
                        '<span class="g-id"></span>' +
                      '</td>' +
                      '<td class="select">' +
                        '<h4>' + choice.title + '</h4>' +
                        '<p class="date"></p>' +
                        '<p class="description"></p>' +
                      '</td>' +
                    '</tr>');

              if (choiceProps.shortcode) {
                template.find('.g-shortcode').html(choiceProps.shortcode);
                template.find('.g-id').html(choiceProps.id);
                template.find('.g').css('background-color', choiceProps.color);
              }

              if (choice.descriptions.length > 0)
                template.find('.description').html(choice.descriptions[0].description);
              else
                template.find('.description').hide();

              if (choice.temporal_bounds)
                template.find('.date').html(
                  Formatting.yyyyMMddToYear(choice.temporal_bounds.from) + ' - ' +
                  Formatting.yyyyMMddToYear(choice.temporal_bounds.to));
              else
                template.find('.date').hide();

              popup.find('.popup-choices table').append(template);
            });

            popup.find('table').on('click', 'tr', function(e) {
              var tr = jQuery(e.target).closest('tr');

              self.fireEvent('change', currentBody, {
                uri: tr.data('uri'),
                status: { value: 'VERIFIED' }
              });

              close();
            });

            marker.bindPopup(popup[0]);
            marker.getPopup().on('close', function() {
              marker.unbindPopup();
            });
            marker.openPopup();
          },

          onSearch = function() {
            var query = searchInput.val().trim();
            if (query.length > 0)
              search(query);
          },

          onFlag = function() {
            self.fireEvent('change', currentBody, {
              uri: false,
              status: { value: 'NOT_IDENTIFIABLE' }
            });
            close();
          },

          search = function(query) {
            resultsList.empty();
            markerLayer.clearLayers();
            API.searchPlaces(query).done(function(response) {
              // TODO dummy only
              if (response.total > 0) {
                resultsHeader.html("Total: " + response.total + ", took " + response.took);

                jQuery.each(response.items, function(idx, place) {
                  var coord = (place.representative_point) ?
                        [ place.representative_point[1], place.representative_point[0] ] :
                        false,

                      result = new Result(resultsList, place),

                      marker = (coord) ? L.marker(coord).addTo(markerLayer) : false;

                  if (marker) {
                    result.on('click', function() {
                      openPopup(marker, place);
                    });

                    marker.on('click', function(e) {
                      openPopup(marker, place);
                    });
                  }
                });
              } else {
                // Try again with a fuzzy search (unless this is a fuzzy search already)
                if (query.indexOf('~') < 0) {
                  search(query + '~');
                }
              }
            });
          },

          open = function(quote, placeBody) {
            currentBody = placeBody;

            searchInput.val(quote);
            element.show();
            map.invalidateSize();
            search(quote);
          },

          close = function() {
            currentBody = false;
            element.hide();
          };

    btnFlag.click(onFlag);
    btnCancel.click(close);
    searchInput.keyup(function(e) { if (e.which === 13) onSearch(); });

    this.open = open;
    HasEvents.apply(this);
  };
  GeoresolutionPanel.prototype = Object.create(HasEvents.prototype);

  return GeoresolutionPanel;

});