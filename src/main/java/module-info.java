module com.xen.oslab.osgui {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.xen.oslab.osgui to javafx.fxml;
    exports com.xen.oslab.osgui;
}