/*
 * Created by IntelliJ IDEA.
 * User: kinabalu
 * Date: Oct 9, 2009
 * Time: 11:35:02 AM
 */
package com.mysticcoders.mysticpaste.web.pages.view;

import com.mysticcoders.mysticpaste.web.pages.BasePage;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.string.Strings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class AutoCompleteExample extends BasePage {


    private static List<String> values = new ArrayList<String>();

    private String country;

    public AutoCompleteExample() {

        Form form = new Form("form");
        add(form);
                
        final AutoCompleteTextField field = new AutoCompleteTextField<String>("field",
                new PropertyModel<String>(AutoCompleteExample.this, "country")) {
            
            @Override
            protected Iterator<String> getChoices(String input) {
                if (Strings.isEmpty(input)) {
                    List<String> emptyList = Collections.emptyList();
                    return emptyList.iterator();
                }

                List<String> keyMatches = new ArrayList<String>(10);

                for (String value : values) {
                    if (value.toUpperCase().startsWith(input.toUpperCase())) {
                        keyMatches.add(value);
                        if (keyMatches.size() == 10) {
                            break;
                        }
                    }
                }

                return keyMatches.iterator();
            }
        };
        form.add(field);

    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    static {
        values = new ArrayList<String>();
        values.add( "Afghanistan");
        values.add( "Albania");
        values.add( "Algeria");
        values.add( "American Samoa");
        values.add( "Andorra");
        values.add( "Angola");
        values.add( "Anguilla");
        values.add( "Antarctica");
        values.add( "Antigua and Barbuda");
        values.add( "Argentina");
        values.add( "Armenia");
        values.add( "Aruba");
        values.add( "Australia");
        values.add( "Austria");
        values.add( "Azerbaijan");
        values.add( "Bahamas");
        values.add( "Bahrain");
        values.add( "Bangladesh");
        values.add( "Barbados");
        values.add( "Belarus");
        values.add( "Belgium");
        values.add( "Belize");
        values.add( "Benin");
        values.add( "Bermuda");
        values.add( "Bhutan");
        values.add( "Bolivia");
        values.add( "Bosnia and Herzegovina");
        values.add( "Botswana");
        values.add( "Bouvet Island");
        values.add( "Brazil");
        values.add( "British Indian Ocean Territory");
        values.add( "Brunei Darussalam");
        values.add( "Bulgaria");
        values.add( "Burkina Faso");
        values.add( "Burundi");
        values.add( "Cambodia");
        values.add( "Cameroon");
        values.add( "Canada");
        values.add( "Cape Verde");
        values.add( "Cayman Islands");
        values.add( "Central African Republic");
        values.add( "Chad");
        values.add( "Chile");
        values.add( "China");
        values.add( "Christmas Island");
        values.add( "Cocos (Keeling) Islands");
        values.add( "Colombia");
        values.add( "Comoros");
        values.add( "Congo");
        values.add( "Congo, the Democratic Republic of the");
        values.add( "Cook Islands");
        values.add( "Costa Rica");
        values.add( "Cote D'Ivoire");
        values.add( "Croatia");
        values.add( "Cuba");
        values.add( "Cyprus");
        values.add( "Czech Republic");
        values.add( "Denmark");
        values.add( "Djibouti");
        values.add( "Dominica");
        values.add( "Dominican Republic");
        values.add( "Ecuador");
        values.add( "Egypt");
        values.add( "El Salvador");
        values.add( "Equatorial Guinea");
        values.add( "Eritrea");
        values.add( "Estonia");
        values.add( "Ethiopia");
        values.add( "Falkland Islands (Malvinas)");
        values.add( "Faroe Islands");
        values.add( "Fiji");
        values.add( "Finland");
        values.add( "France");
        values.add( "French Guiana");
        values.add( "French Polynesia");
        values.add( "French Southern Territories");
        values.add( "Gabon");
        values.add( "Gambia");
        values.add( "Georgia");
        values.add( "Germany");
        values.add( "Ghana");
        values.add( "Gibraltar");
        values.add( "Greece");
        values.add( "Greenland");
        values.add( "Grenada");
        values.add( "Guadeloupe");
        values.add( "Guam");
        values.add( "Guatemala");
        values.add( "Guinea");
        values.add( "Guinea-Bissau");
        values.add( "Guyana");
        values.add( "Haiti");
        values.add( "Heard Island and Mcdonald Islands");
        values.add( "Holy See (Vatican City State)");
        values.add( "Honduras");
        values.add( "Hong Kong");
        values.add( "Hungary");
        values.add( "Iceland");
        values.add( "India");
        values.add( "Indonesia");
        values.add( "Iran, Islamic Republic of");
        values.add( "Iraq");
        values.add( "Ireland");
        values.add( "Israel");
        values.add( "Italy");
        values.add( "Jamaica");
        values.add( "Japan");
        values.add( "Jordan");
        values.add( "Kazakhstan");
        values.add( "Kenya");
        values.add( "Kiribati");
        values.add( "Korea, Democratic People's Republic of");
        values.add( "Korea, Republic of");
        values.add( "Kuwait");
        values.add( "Kyrgyzstan");
        values.add( "Lao People's Democratic Republic");
        values.add( "Latvia");
        values.add( "Lebanon");
        values.add( "Lesotho");
        values.add( "Liberia");
        values.add( "Libyan Arab Jamahiriya");
        values.add( "Liechtenstein");
        values.add( "Lithuania");
        values.add( "Luxembourg");
        values.add( "Macao");
        values.add( "Macedonia, the Former Yugoslav Republic of");
        values.add( "Madagascar");
        values.add( "Malawi");
        values.add( "Malaysia");
        values.add( "Maldives");
        values.add( "Mali");
        values.add( "Malta");
        values.add( "Marshall Islands");
        values.add( "Martinique");
        values.add( "Mauritania");
        values.add( "Mauritius");
        values.add( "Mayotte");
        values.add( "Mexico");
        values.add( "Micronesia, Federated States of");
        values.add( "Moldova, Republic of");
        values.add( "Monaco");
        values.add( "Mongolia");
        values.add( "Montserrat");
        values.add( "Morocco");
        values.add( "Mozambique");
        values.add( "Myanmar");
        values.add( "Namibia");
        values.add( "Nauru");
        values.add( "Nepal");
        values.add( "Netherlands");
        values.add( "Netherlands Antilles");
        values.add( "New Caledonia");
        values.add( "New Zealand");
        values.add( "Nicaragua");
        values.add( "Niger");
        values.add( "Nigeria");
        values.add( "Niue");
        values.add( "Norfolk Island");
        values.add( "Northern Mariana Islands");
        values.add( "Norway");
        values.add( "Oman");
        values.add( "Pakistan");
        values.add( "Palau");
        values.add( "Palestinian Territory, Occupied");
        values.add( "Panama");
        values.add( "Papua New Guinea");
        values.add( "Paraguay");
        values.add( "Peru");
        values.add( "Philippines");
        values.add( "Pitcairn");
        values.add( "Poland");
        values.add( "Portugal");
        values.add( "Puerto Rico");
        values.add( "Qatar");
        values.add( "Reunion");
        values.add( "Romania");
        values.add( "Russian Federation");
        values.add( "Rwanda");
        values.add( "Saint Helena");
        values.add( "Saint Kitts and Nevis");
        values.add( "Saint Lucia");
        values.add( "Saint Pierre and Miquelon");
        values.add( "Saint Vincent and the Grenadines");
        values.add( "Samoa");
        values.add( "San Marino");
        values.add( "Sao Tome and Principe");
        values.add( "Saudi Arabia");
        values.add( "Senegal");
        values.add( "Serbia and Montenegro");
        values.add( "Seychelles");
        values.add( "Sierra Leone");
        values.add( "Singapore");
        values.add( "Slovakia");
        values.add( "Slovenia");
        values.add( "Solomon Islands");
        values.add( "Somalia");
        values.add( "South Africa");
        values.add( "South Georgia and the South Sandwich Islands");
        values.add( "Spain");
        values.add( "Sri Lanka");
        values.add( "Sudan");
        values.add( "Suriname");
        values.add( "Svalbard and Jan Mayen");
        values.add( "Swaziland");
        values.add( "Sweden");
        values.add( "Switzerland");
        values.add( "Syrian Arab Republic");
        values.add( "Taiwan, Province of China");
        values.add( "Tajikistan");
        values.add( "Tanzania, United Republic of");
        values.add( "Thailand");
        values.add( "Timor-Leste");
        values.add( "Togo");
        values.add( "Tokelau");
        values.add( "Tonga");
        values.add( "Trinidad and Tobago");
        values.add( "Tunisia");
        values.add( "Turkey");
        values.add( "Turkmenistan");
        values.add( "Turks and Caicos Islands");
        values.add( "Tuvalu");
        values.add( "Uganda");
        values.add( "Ukraine");
        values.add( "United Arab Emirates");
        values.add( "United Kingdom");
        values.add( "United States");
        values.add( "United States Minor Outlying Islands");
        values.add( "Uruguay");
        values.add( "Uzbekistan");
        values.add( "Vanuatu");
        values.add( "Venezuela");
        values.add( "Viet Nam");
        values.add( "Virgin Islands, British");
        values.add( "Virgin Islands, U.S.");
        values.add( "Wallis and Futuna");
        values.add( "Western Sahara");
        values.add( "Yemen");
        values.add( "Zambia");
        values.add( "Zimbabwe");
    }
}