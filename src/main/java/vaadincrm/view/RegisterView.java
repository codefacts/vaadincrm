package vaadincrm.view;

import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.data.validator.*;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.*;
import com.vaadin.ui.Component;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import vaadincrm.App;
import vaadincrm.Events;
import vaadincrm.model.*;
import vaadincrm.util.ExceptionUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.*;

public class RegisterView extends VerticalLayout implements View {
    private boolean initialized = false;
    private static final String[] userTypes = new String[]{"Admin", "Area Coordinator", "BR Supervisor", "BR", "Head Office"};
    private final SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        ExceptionUtil.sallowRun(() -> initialize());
    }

    public void initialize() throws ParseException {
        if (!initialized) {
            setMargin(true);
            final Component form = buildForm();
            addComponent(form);
            setComponentAlignment(form, Alignment.BOTTOM_CENTER);
            initialized = true;
        }
    }

    private Component buildForm() throws ParseException {

        final FormLayout form = new FormLayout();
        form.addStyleName("outlined");
        form.setSizeFull();
        form.setSpacing(true);

        //firstName
        final TextField firstName = new TextField("First Name");
        firstName.setWidth(100.0f, Unit.PERCENTAGE);
        firstName.addValidator(new RegexpValidator("^[a-zA-Z][a-zA-Z ]+[a-zA-Z]$", "Min length is 3 and max length is 75 characters."));
        firstName.setRequired(true);
        firstName.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                if (firstName.isEmpty()) {
                    firstName.setRequiredError("First Name is required.");
                }
            }
        });
        firstName.setImmediate(true);
        form.addComponent(firstName);

        //lastName
        final TextField lastName = new TextField("Last Name");
        lastName.setWidth(100.0f, Unit.PERCENTAGE);
        lastName.addValidator(new RegexpValidator("^[a-zA-Z][a-zA-Z ]+[a-zA-Z]$", "Min length is 3 and max length is 75 characters."));
        lastName.setRequired(true);
        lastName.setImmediate(true);
        lastName.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                if (lastName.isEmpty()) {
                    lastName.setRequiredError("Last Name is required.");
                }
            }
        });
        form.addComponent(lastName);

        //Username
        final TextField username = new TextField("Username");
        username.setWidth(100.0f, Unit.PERCENTAGE);
        username.addValidator(new RegexpValidator("^[a-zA-Z][a-zA-Z ]+[a-zA-Z]$", "Min length is 3 and max length is 75 characters."));
        username.setRequired(true);
        username.setImmediate(true);
        username.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                if (username.isEmpty()) {
                    username.setRequiredError("Username is required.");
                }
            }
        });
        form.addComponent(username);

        //Password
        final PasswordField password = new PasswordField("Password");
        password.setWidth(100.0f, Unit.PERCENTAGE);
        password.addValidator(new RegexpValidator(".{3,75}", "Min length is 3 and max length is 75 characters."));
        password.setRequired(true);
        password.setImmediate(true);
        password.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                System.out.println(password.getValue());
                if (password.isEmpty()) {
                    password.setRequiredError("Password is required.");
                }
            }
        });
        form.addComponent(password);

        Status status = new Status();
        //Confirm Password
        final PasswordField password2 = new PasswordField("Confirm Password");
        password2.setWidth(100.0f, Unit.PERCENTAGE);
        password2.addValidator(new RegexpValidator(".{3,75}", "Min length is 3 and max length is 75 characters."));
        password2.addValidator(new Validator() {
            @Override
            public void validate(Object value) throws InvalidValueException {
                if (status.changed && !password.isEmpty() && !password.getValue().equals(password2.getValue())) {
                    throw new InvalidValueException("Password does not match.");
                }
            }
        });
        password2.setRequired(true);
        password2.setImmediate(true);
        password2.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                status.changed = true;
                if (password2.isEmpty()) {
                    password2.setRequiredError("Please confirm password.");
                }
            }
        });
        form.addComponent(password2);

        //Phone
        final TextField phone = new TextField("Phone");
        phone.setWidth(100.0f, Unit.PERCENTAGE);
        phone.addValidator(new RegexpValidator("\\d{11}", "Phone number must be 11 characters length and a number."));
        phone.setRequired(true);
        phone.setImmediate(true);
        phone.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                if (phone.isEmpty()) {
                    phone.setRequiredError("Phone is required.");
                }
            }
        });
        form.addComponent(phone);

        //Email
        final TextField email = new TextField("Email");
        email.setWidth(100.0f, Unit.PERCENTAGE);
        email.addValidator(new EmailValidator("Invalid Email"));
        email.setImmediate(true);
        form.addComponent(email);

        //Date Of Birth
        PopupDateField dateOfBirth = new PopupDateField();
        dateOfBirth.setCaption("Date of Birth");
//        dateOfBirth.addValidator(new DateRangeValidator("Date must be within 1920 and 2000", dateformat.parse("1920-00-00"), dateformat.parse("2000-00-00"), null));
        dateOfBirth.setRangeStart(dateformat.parse("1920-00-00"));
        dateOfBirth.setRangeEnd(dateformat.parse("2000-00-00"));
        dateOfBirth.setDateOutOfRangeMessage("Date must be within 1920 and 2000");
        dateOfBirth.setRequired(true);
        dateOfBirth.setImmediate(true);
        dateOfBirth.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                if (dateOfBirth.isEmpty()) {
                    dateOfBirth.setRequiredError("Date of Birth is required.");
                }
            }
        });
        form.addComponent(dateOfBirth);

        //Designations
        final Status designationStatus = new Status();
        final NativeSelect designation = designation(designationStatus);
        form.addComponent(designation);

        //Join Date
        PopupDateField joinDate = new PopupDateField();
        joinDate.setCaption("Join Date");
        joinDate.addValidator(new DateRangeValidator("Date must be within 1920 and 2050", dateformat.parse("1920-00-00"), dateformat.parse("2050-00-00"), null));
        joinDate.setRequired(true);
        joinDate.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                if (joinDate.isEmpty()) {
                    joinDate.setRequiredError("Join Date is required.");
                }
            }
        });
        form.addComponent(joinDate);

        final Button button = new Button("Next");
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                boolean valid = true;
                valid = valid && validate(firstName, "First Name");
                valid = valid && validate(lastName, "Last Name");
                valid = valid && validate(username, "Username");
                valid = valid && validate(password, "Password");
                valid = valid && validate(password2, "Confirm Password");
                valid = valid && validate(phone, "Phone");
                if (!email.isEmpty()) {
                    valid = valid && validate(email, "Email");
                }
                valid = valid && validate(dateOfBirth, "Date of Birth");
                valid = valid && validate(joinDate, "Join Date");
                designationStatus.changed = true;
                valid = valid && validate(designation);
                if (!valid) {
                    Notification.show("Please correct the fields with errors.", Notification.Type.ERROR_MESSAGE);
                    return;
                }

                final SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                final JsonObject userInfo = new JsonObject()
                        .put("username", username.getValue())
                        .put("firstName", firstName.getValue())
                        .put("lastName", lastName.getValue())
                        .put("password", password.getValue())
                        .put("email", email.getValue())
                        .put("phone", phone.getValue())
                        .put("dateOfBirth", dateformat.format(dateOfBirth.getValue()))
                        .put("userType", UserType.employee)
                        .put("joinDate", dateformat.format(joinDate.getValue()));

                java.lang.Runnable onSuccess = () -> {
                    //On Success
                    removeAllComponents();
                    initialized = false;
                    ExceptionUtil.sallowRun(() -> initialize());
                    Notification.show("Registration successfull.");
                };

                java.lang.Runnable onError = () -> {
                    Notification.show("Sorry, Error in server. Please try again later.");
                };

                Handler<AsyncResult<Message<Object>>> handler = r -> {
                    if (r.succeeded()) {
                        onSuccess.run();
                    } else {
                        onError.run();
                    }
                };

                switch (Integer.parseInt(designation.getValue() + "")) {
                    case 0:
                        App.bus.send(Events.CREATE_NEW_ADMIN, userInfo, handler);
                        break;
                    case 1:
                        areaCoordinatorWindow(userInfo);
                        break;
                    case 2:
                        brSupervisorWindow(userInfo);
                        break;
                    case 3:
                        brWindow(userInfo);
                        break;
                    case 4:
                        App.bus.send(Events.CREATE_NEW_HEAD_OFFICE, userInfo, handler);
                        break;
                }
            }
        });

        form.addComponent(button);
        return form;
    }

    private void brWindow(JsonObject userInfo) {
        final UI ui = UI.getCurrent();
        class DD {
            public JsonArray brands, houses, towns;
            boolean error = false;
            AtomicInteger count = new AtomicInteger(0);
            Throwable cause;
        }
        final DD d = new DD();

        final java.lang.Runnable then = () -> {
            final int count = d.count.get();
            System.out.println("count: " + count + " Thread: " + Thread.currentThread().getId());
            if (count >= 3) {
                ui.access(() -> {
                    removeAllComponents();
                    final FormLayout form = brForm(userInfo, d.houses, d.towns, d.brands);
                    addComponent(form);
                });
            }
        };

        java.lang.Runnable onError = () -> {
            ui.access(() -> Notification.show("Some error occured. Please try again later.", Notification.Type.ERROR_MESSAGE));
        };

        App.bus.send(Events.FIND_ALL_BRANDS, null, (AsyncResult<Message<JsonArray>> r) -> {
            if (r.succeeded()) {
                d.brands = r.result().body();
                d.count.incrementAndGet();
                then.run();
            } else {
                d.error = true;
                d.cause = r.cause();
                onError.run();
            }
        });

        App.bus.send(Events.FIND_ALL_DISTRIBUTION_HOUSES, null, (AsyncResult<Message<JsonArray>> r) -> {
            if (r.succeeded()) {
                d.houses = r.result().body();
                d.count.incrementAndGet();
                then.run();
            } else {
                d.error = true;
                d.cause = r.cause();
                onError.run();
            }
        });

        App.bus.send(Events.FIND_ALL_TOWNS, null, (AsyncResult<Message<JsonArray>> r) -> {
            if (r.succeeded()) {
                d.towns = r.result().body();
                d.count.incrementAndGet();
                then.run();
            } else {
                d.error = true;
                d.cause = r.cause();
                onError.run();
            }
        });
    }

    private FormLayout brForm(JsonObject userInfo, JsonArray houses, JsonArray towns, JsonArray brands) {
        final FormLayout form = new FormLayout();
        form.addStyleName("outlined");
        form.setSizeFull();
        form.setSpacing(true);

        //Houses
        final Status status = new Status();
        NativeSelect houseSelect = new NativeSelect("Houses");
        houseSelect.addItem(-1);
        houseSelect.setItemCaption(-1, "Select House");
        for (final Object obj : houses) {
            if (obj instanceof JsonObject) {
                final JsonObject h = (JsonObject) obj;
                final long id = h.getLong("id");
                houseSelect.addItem(id);
                houseSelect.setItemCaption(id, h.getString("name"));
            }
        }

        houseSelect.addValidator(new Validator() {
            @Override
            public void validate(Object value) throws InvalidValueException {
                if (value instanceof Integer) {
                    if (status.changed && Integer.parseInt(value + "") < 0) {
                        throw new InvalidValueException("House is required.");
                    }
                }
            }
        });
        houseSelect.setNullSelectionAllowed(false);
        houseSelect.setRequired(true);
        houseSelect.setImmediate(true);
        houseSelect.setValue(-1);

        houseSelect.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                status.changed = true;
            }
        });

        //Towns
        final Status statusTown = new Status();
        NativeSelect townSelect = new NativeSelect("Towns");
        townSelect.addItem(-1);
        townSelect.setItemCaption(-1, "Select Town");
        for (final Object obj : towns) {
            if (obj instanceof JsonObject) {
                final JsonObject h = (JsonObject) obj;
                final long id = h.getLong("id");
                townSelect.addItem(id);
                townSelect.setItemCaption(id, h.getString("name"));
            }
        }

        townSelect.addValidator(new Validator() {
            @Override
            public void validate(Object value) throws InvalidValueException {
                if (value instanceof Integer) {
                    if (statusTown.changed && Integer.parseInt(value + "") < 0) {
                        throw new InvalidValueException("Town is required.");
                    }
                }
            }
        });
        townSelect.setNullSelectionAllowed(false);
        townSelect.setRequired(true);
        townSelect.setImmediate(true);
        townSelect.setValue(-1);

        townSelect.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                statusTown.changed = true;
            }
        });

        //Brands
        final Status statusBrand = new Status();
        NativeSelect brandSelect = new NativeSelect("Brand");
        brandSelect.addItem(-1);
        brandSelect.setItemCaption(-1, "Select Brand");
        for (final Object obj : brands) {
            if (obj instanceof JsonObject) {
                final JsonObject h = (JsonObject) obj;
                final long id = h.getLong("id");
                brandSelect.addItem(id);
                brandSelect.setItemCaption(id, h.getString("name"));
            }
        }

        brandSelect.addValidator(new Validator() {
            @Override
            public void validate(Object value) throws InvalidValueException {
                if (value instanceof Integer) {
                    if (statusBrand.changed && Integer.parseInt(value + "") < 0) {
                        throw new InvalidValueException("Brand is required.");
                    }
                }
            }
        });
        brandSelect.setNullSelectionAllowed(false);
        brandSelect.setRequired(true);
        brandSelect.setImmediate(true);
        brandSelect.setValue(-1);

        brandSelect.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                statusBrand.changed = true;
            }
        });

        form.addComponent(houseSelect);
        form.addComponent(townSelect);
        form.addComponent(brandSelect);


        final Button button = new Button("Finish");
        button.addClickListener(event -> {
            boolean valid = true;
            valid = valid && validate(houseSelect);
            if (!valid) {
                Notification.show("Please correct the fields with errors.", Notification.Type.ERROR_MESSAGE);
                return;
            }
            valid = valid && validate(townSelect);
            if (!valid) {
                Notification.show("Please correct the fields with errors.", Notification.Type.ERROR_MESSAGE);
                return;
            }
            valid = valid && validate(brandSelect);
            if (!valid) {
                Notification.show("Please correct the fields with errors.", Notification.Type.ERROR_MESSAGE);
                return;
            }
            if (valid) {
                userInfo.put("distributionHouse", houseSelect.getValue());
                userInfo.put("town", townSelect.getValue());
                userInfo.put("brand", brandSelect.getValue());
                final UI ui = UI.getCurrent();
                App.bus.send(Events.CREATE_NEW_BR, userInfo, r -> {
                    if (r.succeeded()) {
                        ui.access(() -> Notification.show("Success"));
                    } else {
                        ui.access(() -> Notification.show("Error", Notification.Type.ERROR_MESSAGE));
                    }
                });
            }
        });

        form.addComponent(button);

        return form;
    }

    private void brSupervisorWindow(JsonObject userInfo) {
        final UI ui = UI.getCurrent();

        final java.util.function.Consumer<JsonArray> then = houses -> {
            ui.access(() -> {
                removeAllComponents();
                final FormLayout form = brSupervisorForm(userInfo, houses);
                addComponent(form);
            });
        };

        java.lang.Runnable onError = () -> {
            ui.access(() -> Notification.show("Some error occured. Please try again later.", Notification.Type.ERROR_MESSAGE));
        };

        App.bus.send(Events.FIND_ALL_DISTRIBUTION_HOUSES, null, (AsyncResult<Message<JsonArray>> r) -> {
            if (r.succeeded()) {
                then.accept(r.result().body());
            } else {
                onError.run();
            }
        });
    }

    private FormLayout brSupervisorForm(JsonObject userInfo, JsonArray houses) {
        final FormLayout form = new FormLayout();
        form.addStyleName("outlined");
        form.setSizeFull();
        form.setSpacing(true);

        //Houses
        final Status status = new Status();
        NativeSelect houseSelect = new NativeSelect("Areas");
        houseSelect.addItem(-1);
        houseSelect.setItemCaption(-1, "Select House");
        for (final Object obj : houses) {
            if (obj instanceof JsonObject) {
                final JsonObject h = (JsonObject) obj;
                final long id = h.getLong("id");
                houseSelect.addItem(id);
                houseSelect.setItemCaption(id, h.getString("name"));
            }
        }

        houseSelect.addValidator(new Validator() {
            @Override
            public void validate(Object value) throws InvalidValueException {
                if (value instanceof Integer) {
                    if (status.changed && Integer.parseInt(value + "") < 0) {
                        throw new InvalidValueException("House is required.");
                    }
                }
            }
        });
        houseSelect.setNullSelectionAllowed(false);
        houseSelect.setRequired(true);
        houseSelect.setImmediate(true);
        houseSelect.setValue(-1);

        houseSelect.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                status.changed = true;
            }
        });

        form.addComponent(houseSelect);


        final Button button = new Button("Finish");
        button.addClickListener(event -> {
            boolean valid = true;
            valid = valid && validate(houseSelect);
            if (!valid) {
                Notification.show("Please correct the fields with errors.", Notification.Type.ERROR_MESSAGE);
                return;
            }
            if (valid) {
                userInfo.put("house", houseSelect.getValue());
                final UI ui = UI.getCurrent();
                App.bus.send(Events.CREATE_NEW_BR_SUPERVISOR, userInfo, r -> {
                    if (r.succeeded()) {
                        ui.access(() -> Notification.show("Success"));
                    } else {
                        ui.access(() -> Notification.show("Error", Notification.Type.ERROR_MESSAGE));
                    }
                });
            }
        });

        form.addComponent(button);

        return form;
    }

    private void areaCoordinatorWindow(JsonObject userInfo) {
        final UI ui = UI.getCurrent();

        final java.util.function.Consumer<JsonArray> then = areas -> {
            ui.access(() -> {
                removeAllComponents();
                final FormLayout form = areaCoordinatorForm(userInfo, areas);
                addComponent(form);
            });
        };

        java.lang.Runnable onError = () -> {
            ui.access(() -> Notification.show("Some error occured. Please try again later.", Notification.Type.ERROR_MESSAGE));
        };

        App.bus.send(Events.FIND_ALL_BRANDS, null, (AsyncResult<Message<JsonArray>> r) -> {
            if (r.succeeded()) {
                then.accept(r.result().body());
            } else {
                onError.run();
            }
        });
    }

    private FormLayout areaCoordinatorForm(JsonObject userInfo, JsonArray areas) {
        final FormLayout form = new FormLayout();
        form.addStyleName("outlined");
        form.setSizeFull();
        form.setSpacing(true);

        //Areas
        final Status status = new Status();
        NativeSelect areaSelect = new NativeSelect("Areas");
        areaSelect.addItem(-1);
        areaSelect.setItemCaption(-1, "Select Area");
        for (final Object obj : areas) {
            if (obj instanceof JsonObject) {
                final JsonObject h = (JsonObject) obj;
                final long id = h.getLong("id");
                areaSelect.addItem(id);
                areaSelect.setItemCaption(id, h.getString("name"));
            }
        }

        areaSelect.addValidator(new Validator() {
            @Override
            public void validate(Object value) throws InvalidValueException {
                if (value instanceof Integer) {
                    if (status.changed && Integer.parseInt(value + "") < 0) {
                        throw new InvalidValueException("Area is required.");
                    }
                }
            }
        });
        areaSelect.setNullSelectionAllowed(false);
        areaSelect.setRequired(true);
        areaSelect.setImmediate(true);
        areaSelect.setValue(-1);

        areaSelect.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                status.changed = true;
            }
        });

        form.addComponent(areaSelect);


        final Button button = new Button("Finish");
        button.addClickListener(event -> {
            boolean valid = true;
            valid = valid && validate(areaSelect);
            if (!valid) {
                Notification.show("Please correct the fields with errors.", Notification.Type.ERROR_MESSAGE);
                return;
            }
            if (valid) {
                userInfo.put("area", areaSelect.getValue());
                final UI ui = UI.getCurrent();
                App.bus.send(Events.CREATE_NEW_AREA_COORDINATOR, userInfo, r -> {
                    if (r.succeeded()) {
                        ui.access(() -> Notification.show("Success"));
                    } else {
                        ui.access(() -> Notification.show("Error", Notification.Type.ERROR_MESSAGE));
                    }
                });
            }
        });

        form.addComponent(button);

        return form;
    }

    private boolean validate(NativeSelect select) {
        boolean valid = false;
        try {
            select.validate();
            valid = true;
        } catch (Validator.EmptyValueException ex) {
            select.setRequiredError("This field is required.");
        } catch (Validator.InvalidValueException ex) {
            select.setValidationVisible(true);
            Notification.show("Usertype is required.", Notification.Type.ERROR_MESSAGE);
        }
        return valid;
    }

    private boolean validate(PopupDateField dateField, String fieldname) {
        boolean valid = false;
        try {
            dateField.validate();
            valid = true;
        } catch (Validator.EmptyValueException ex) {
            dateField.setRequiredError(fieldname + " is required.");
        } catch (Validator.InvalidValueException ex) {
            dateField.setValidationVisible(true);
        }
        return valid;
    }

    private boolean validate(TextField textField, String fieldname) {
        boolean valid = false;
        try {
            textField.validate();
            valid = true;
        } catch (Validator.EmptyValueException ex) {
            textField.setRequiredError(fieldname + " is required.");
        } catch (Validator.InvalidValueException ex) {
            textField.setValidationVisible(true);
        }
        return valid;
    }

    private boolean validate(PasswordField textField, String fieldname) {
        boolean valid = false;
        try {
            textField.validate();
            valid = true;
        } catch (Validator.EmptyValueException ex) {
            textField.setRequiredError(fieldname + " is required.");
        } catch (Validator.InvalidValueException ex) {
            textField.setValidationVisible(true);
        }
        return valid;
    }

    private NativeSelect designation(Status status) {

        NativeSelect designations = new NativeSelect("User Type");
        designations.addItem(-1);
        designations.setItemCaption(-1, "Select User Type");
        for (int i = 0; i < userTypes.length; i++) {
            designations.addItem(i);
            designations.setItemCaption(i, userTypes[i]);
        }

        designations.addValidator(new Validator() {
            @Override
            public void validate(Object value) throws InvalidValueException {
                if (value instanceof Integer) {
                    if (status.changed && Integer.parseInt(value + "") < 0) {
                        throw new InvalidValueException("Usertype is required.");
                    }
                }
            }
        });
        designations.setNullSelectionAllowed(false);
        designations.setRequired(true);
        designations.setImmediate(true);
        designations.setValue(-1);

        designations.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                status.changed = true;
            }
        });
        return designations;
    }

    private static class Status {
        public boolean changed = false;
    }

    ;
}
