package com.mycompany.capturatest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LogItem {
    /* Object representing an entry in a maintenance log
     * Attributes:
     *   - logItemNo (Integer): Number of entry in the maintenance log (max 3).
     *   - BaseLogItem (Class): Abstract item used to create discrepancies and corrective actions
     *       - station (String): IATA code of the station where the discrepancy was reported.
     *       - ataChapter (String): ATA Chapter corresponding to the discrepancy.
     *       - reportedBy (Class): Object representing who reported the discrepancy.
     *           - name (String): Name of who reports the discrepancy.
     *           - license (String): Their license number.
     *       - dateReported (Date): Date in which the report was made.
     *       - description (String): Description of the discrepancy.
     *   - discrepancy (BaseLogItem): Represents the discrepancy report in the maint. log.
     *       - flightNo (String): Flight number of the flight in which the report was made.
     *       - operationLog (Integer): Folio of the corresponding operation log.
     *   - correctiveAction (BaseLogItem): Represents the corrective action report in log.
     *       - deferred (boolean): Indicates if the discrepancy was deferred.
     *       - deferralBasis (String): If it was deferred under which MEL Chapter.
     *   - componentsChanged (List<ComponentChange>): Containing component changes.
     *       - ComponentChange (Class): Object representing a component change.
     *           - componentRemoved (Boolean): A component was removed.
     *           - removedPartNumber (String): Part number of the installed component.
     *           - removedSerialNumber (String): Serial number of the installed component.
     *           - componentInstalled (Boolean): A component was installed.
     *           - installedPartNumber (String): Part number of the installed component.
     *           - installedSerialNumber (String): Serial number of the installed component
     */
    private int logItemNumber;
    private Discrepancy discrepancy;
    private CorrectiveAction correctiveAction;
    private ComponentsChanged componentsChanged;
    private boolean canceled;

    LogItem(int logItemNo) {
        logItemNumber = logItemNo;
        discrepancy = new Discrepancy();
        correctiveAction = new CorrectiveAction();
        componentsChanged = new ComponentsChanged();
        canceled = false;
    }

    int getLogItemNumber() {
        return logItemNumber;
    }

    Discrepancy getDiscrepancy() {
        return discrepancy;
    }

    CorrectiveAction getCorrectiveAction() {
        return correctiveAction;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public JSONObject toJSON() {
        // Magic strings bad
        final String discrepancyJSON = "discrepancy";
        final String correctiveActionJSON = "correctiveAction";
        final String componentsChangedJSON = "componentsChanged";
        final String canceledJSON = "canceledItem";
        JSONObject jsonObject = new JSONObject();
        try {
            if (canceled) {
                // If the whole item is cancelled only add this
                jsonObject.put(canceledJSON, true);
            } else {
                jsonObject.put(discrepancyJSON, discrepancy);
                jsonObject.put(correctiveActionJSON, correctiveAction);
                if (componentsChanged.numberOfComponentChanges() > 0)
                    jsonObject.put(componentsChangedJSON, componentsChanged);
            }
        } catch(JSONException ex) {
            ex.printStackTrace();
        }
        return jsonObject;
    }

    private abstract class BaseLogItem {
        private String station;
        private String ataChapter;
        private Date dateReported;
        private Employee reportedBy;
        private String description;
        private boolean canceled;

        final String stationJSON = "station";
        final String ataJSON = "ataChapter";
        final String dateReportedJSON = "date";
        final String reportedByJSON = "reportedBy";
        final String descriptionJSON = "description";
        final String canceledJSON = "canceled";
        BaseLogItem() {
            reportedBy = new Employee();
            canceled = false;
        }

        public void setStation(String station) {
            this.station = station;
        }

        public String getStation() {
            return station;
        }

        public String getAtaChapter() {
            return ataChapter;
        }

        public void setAtaChapter(String ataChapter) {
            this.ataChapter = ataChapter;
        }

        public Date getDate() {
            return dateReported;
        }

        public void setDate(Date date) {
            dateReported = date;
        }

        public Employee getEmployee() {
            return reportedBy;
        }

        String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        boolean isNotCanceled() {
            return !canceled;
        }

        public void setCanceled(boolean cancel) {
            canceled = cancel;
        }

        public JSONObject toJSON() {
            JSONObject jsonObject = new JSONObject();
            try {
                if (canceled) {
                    // If item is canceled we don't need to add anything to it
                    jsonObject.put(canceledJSON, true);
                } else {
                    jsonObject.put(stationJSON, station);
                    if (ataChapter != null)
                        jsonObject.put(ataJSON, ataChapter);

                    // Convert date to a string
                    String pattern = "dd/MM/yyyy";
                    DateFormat df = new SimpleDateFormat(pattern,
                                                         new Locale("es", "mx"));
                    String dateString = df.format(dateReported);
                    jsonObject.put(dateReportedJSON, dateString);

                    jsonObject.put(reportedByJSON, reportedBy.toJSON());
                    jsonObject.put(descriptionJSON, description);
                }
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
            return jsonObject;
        }
    }

    protected class Discrepancy extends BaseLogItem {
        private String flightNumber;
        private String operationLog;

        final String flightNumberJSON = "flightNumber";
        final String operationLogJSON = "operationLog";

        Discrepancy() {
            super();
        }

        public String getFlightNumber() {
            return flightNumber;
        }

        public void setFlightNumber(String flightNumber) {
            this.flightNumber = flightNumber;
        }

        public String getOperationLog() {
            return operationLog;
        }

        public void setOperationLog(String operationLog) {
            this.operationLog = operationLog;
        }

        @Override
        public JSONObject toJSON() {
            JSONObject jsonObject = super.toJSON();
            if (isNotCanceled()) {
                // If it's not canceled add the extra parameters
                try {
                    if (flightNumber != null)
                        jsonObject.put(flightNumberJSON, flightNumber);
                    if (operationLog != null)
                        jsonObject.put(operationLogJSON, operationLog);
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            } // If it's canceled there's no point in adding anything else
            return jsonObject;
        }
    }

    protected class CorrectiveAction extends BaseLogItem {
        private boolean deferred;
        private String deferralBasis;

        final String deferredJSON = "deferred";
        final String deferralBasisJSON = "deferralBasis";

        CorrectiveAction() {
            super();
            deferred = false;
        }

        public boolean isDeferred() {
            return deferred;
        }

        public void setDeferred(boolean deferred) {
            this.deferred = deferred;
        }

        @Override
        public JSONObject toJSON() {
            JSONObject jsonObject = super.toJSON();
            if (isNotCanceled()) {
                // If it's not canceled add the extra parameters
                try {
                    jsonObject.put(deferredJSON, deferred);
                    if (deferred)
                        jsonObject.put(deferralBasisJSON, deferralBasis);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } // If it's canceled there's no point in adding anything else
            return jsonObject;
        }
    }

    protected class Employee {
        private String employeeName;
        private String employeeLicense;
        private boolean crew;
        final String employeeNameJSON = "name";
        final String employeeLicenseJSON = "license";
        final String employeeIsCrewJSON = "isCrew";

        Employee () {
            crew = false;
        }

        public String getEmployeeName() {
            return employeeName;
        }

        public void setEmployeeName(String name) {
            // TODO: Add employee name to the local sqlite db and try to retrieve license from name
            employeeName = name;
        }

        public String getEmployeeLicense() {
            return employeeLicense;
        }

        public void setEmployeeLicense(String license) {
            // TODO: Add employee license to the local sqlite db and retrieve name from license
            employeeLicense = license;
        }

        public boolean isCrew() {
            return crew;
        }

        public void setCrew(boolean isCrew) {
            //TODO: Add logic for setting data to sqlite db
            this.crew = isCrew;
        }

        JSONObject toJSON() {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(employeeNameJSON, employeeName);
                if (employeeLicense != null)
                    jsonObject.put(employeeLicenseJSON, employeeLicense);
                jsonObject.put(employeeIsCrewJSON, crew);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject;
        }
    }

    protected class ComponentsChanged {
        private List<ComponentChange> componentChangeList;

        ComponentsChanged() {
            componentChangeList = new ArrayList<>();
        }

        public int numberOfComponentChanges() {
            return componentChangeList.size();
        }

        public JSONArray toJSON() {
            JSONArray jsonArray = new JSONArray();
            // Loop through all component changes, call their toJSON method and add it to the array
            for (ComponentChange change : componentChangeList) {
                jsonArray.put(change.toJSON());
            }
            return jsonArray;
        }

        protected class ComponentChange {
            private boolean componentInstalled;
            private String installedPartNumber;
            private String installedSerialNumber;
            private boolean componentRemoved;
            private String removedPartNumber;
            private String removedSerialNumber;

            final String partNumberJSON = "partNumber";
            final String serialNumberJSON = "serialNumber";
            final String componentInstalledJSON = "installed";
            final String componentRemovedJSON = "removed";

            public boolean isComponentInstalled() {
                return componentInstalled;
            }

            public void setComponentInstalled(boolean componentInstalled) {
                this.componentInstalled = componentInstalled;
            }

            public String getInstalledPartNumber() {
                return installedPartNumber;
            }

            public void setInstalledPartNumber(String installedPartNumber) {
                // Prevent from setting if no component was installed.
                if (componentInstalled) this.installedPartNumber = installedPartNumber;
            }

            public String getInstalledSerialNumber() {
                return installedSerialNumber;
            }

            public void setInstalledSerialNumber(String installedSerialNumber) {
                // Prevent from setting if no component was installed.
                if (componentInstalled) this.installedSerialNumber = installedSerialNumber;
            }

            public boolean isComponentRemoved() {
                return componentRemoved;
            }

            public void setComponentRemoved(boolean componentRemoved) {
                this.componentRemoved = componentRemoved;
            }

            public String getRemovedPartNumber() {
                return removedPartNumber;
            }

            public void setRemovedPartNumber(String removedPartNumber) {
                // Prevent from setting if no component was removed.
                if (componentRemoved) this.removedPartNumber = removedPartNumber;
            }

            public String getRemovedSerialNumber() {
                return removedSerialNumber;
            }

            public void setRemovedSerialNumber(String removedSerialNumber) {
                // Prevent from setting if no component was removed.
                if (componentRemoved) this.removedSerialNumber = removedSerialNumber;
            }

            JSONObject toJSON() {
                JSONObject jsonObject = new JSONObject();
                if (componentInstalled) {
                    JSONObject installedObject = new JSONObject();
                    try {
                        installedObject.put(partNumberJSON, installedPartNumber);
                        installedObject.put(serialNumberJSON, installedSerialNumber);
                        jsonObject.put(componentInstalledJSON, installedObject);
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                }
                if (componentRemoved) {
                    JSONObject removedObject = new JSONObject();
                    try {
                        removedObject.put(partNumberJSON, removedPartNumber);
                        removedObject.put(serialNumberJSON, removedSerialNumber);
                        jsonObject.put(componentRemovedJSON, removedObject);
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                }
                return jsonObject;
            }
        }
    }
}
