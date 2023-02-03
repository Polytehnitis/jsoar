module org.jsoar.soarunit
{
    requires com.google.common;
    requires info.picocli;
    requires java.datatransfer;
    requires java.desktop;
    requires junit;
    requires org.apache.commons.io;
    requires org.jsoar.core;
    requires sml;
    requires spring.core;
    
    provides org.jsoar.util.commands.SoarCommandProvider with org.jsoar.soarunit.SoarUnitCommand.Provider;
    
    exports org.jsoar.soarunit;
    exports org.jsoar.soarunit.jsoar;
    exports org.jsoar.soarunit.junit;
    exports org.jsoar.soarunit.sml;
    exports org.jsoar.soarunit.ui;
    
    opens org.jsoar.soarunit to info.picocli;
}
