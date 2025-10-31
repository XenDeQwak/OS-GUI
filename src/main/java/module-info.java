module com.xen.oslab.osgui {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;


    opens com.xen.oslab to javafx.fxml;
    exports com.xen.oslab;
    exports com.xen.oslab.objects;
    opens com.xen.oslab.objects to javafx.fxml;
    exports com.xen.oslab.managers;
    opens com.xen.oslab.managers to javafx.fxml;
}