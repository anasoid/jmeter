/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.jmeter.gui;

import static org.apache.jmeter.util.JMeterUtils.getResString;
import static org.apache.jmeter.util.JMeterUtils.labelFor;
import static org.apiguardian.api.API.Status.DEPRECATED;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;
import static org.apiguardian.api.API.Status.INTERNAL;

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.tree.TreeNode;

import org.apache.jmeter.gui.tree.JMeterTreeNode;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.testelement.Skippable;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.property.StringProperty;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.visualizers.Printable;
import org.apache.jorphan.gui.JFactory;
import org.apache.jorphan.gui.layout.VerticalLayout;
import org.apiguardian.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.miginfocom.swing.MigLayout;

/**
 * This abstract class takes care of the most basic functions necessary to
 * create a viable JMeter GUI component. It extends JPanel and implements
 * JMeterGUIComponent. This abstract class is, in turn, extended by several
 * other abstract classes that create different classes of GUI components for
 * JMeter (Visualizers, Timers, Samplers, Modifiers, Controllers, etc).
 *
 * @see org.apache.jmeter.gui.JMeterGUIComponent
 * @see org.apache.jmeter.config.gui.AbstractConfigGui
 * @see org.apache.jmeter.assertions.gui.AbstractAssertionGui
 * @see org.apache.jmeter.control.gui.AbstractControllerGui
 * @see org.apache.jmeter.timers.gui.AbstractTimerGui
 * @see org.apache.jmeter.visualizers.gui.AbstractVisualizer
 * @see org.apache.jmeter.samplers.gui.AbstractSamplerGui
 *
 */
public abstract class AbstractJMeterGuiComponent extends JPanel implements JMeterGUIComponent, Printable {
    private static final long serialVersionUID = 241L;

    /** Logging */
    private static final Logger log = LoggerFactory.getLogger(AbstractJMeterGuiComponent.class);

    /** Flag indicating whether or not this component is enabled. */
    private boolean enabled = true;

    /**
     *  A GUI panel containing the name of this component.
     * @deprecated use {@link #getName()} or {@link AbstractJMeterGuiComponent#createTitleLabel()} for better alignment of the fields
     **/
    @API(status = INTERNAL, since = "5.2.0")
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    protected NamePanel namePanel;

    private final JTextArea commentField = JFactory.tabMovesFocus(new JTextArea());

    /**
     * skipped field.
     */
    private final JTextArea skippedField = JFactory.tabMovesFocus(new JTextArea());

    /**
     * skipped Panel.
     */
    private final JPanel skipPanel = new JPanel(new MigLayout("fillx, wrap 2, insets 0",
        "[][fill,grow]"));

    /**
     * Advanced Panel.
     */
    private final JPanel advancedPanel = new JPanel();

    private boolean advancedPanelVisible = true;

    /**
     * display advanced Panel.
     */
    private final JButton advancedButton = new JButton("");

    /**
     * When constructing a new component, this takes care of basic tasks like setting up the Name
     * Panel and assigning the class's static label as the name to start.
     */
    protected AbstractJMeterGuiComponent() {
        namePanel = new NamePanel();
        init();
    }

    /**
     * Provides a default implementation for setting the name property. It's unlikely developers will
     * need to override.
     */
    @Override
    public void setName(String name) {
        namePanel.setName(name);
    }

    /**
     * Provides a default implementation for setting the comment property. It's
     * unlikely developers will need to override.
     *
     * @param comment
     *            The comment for the property
     */
    public void setComment(String comment) {
        commentField.setText(comment);
    }


    /**
     * Provides a default implementation for setting the comment property. It's unlikely developers
     * will need to override.
     *
     * @param skipped The skipped for the property
     */
    public void setSkipped(String skipped) {
        skippedField.setText(skipped);
    }

    /**
     * Provides a default implementation for the enabled property. It's unlikely developers will need
     * to override.
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Provides a default implementation for the enabled property. It's unlikely developers will need
     * to override.
     */
    @Override
    public void setEnabled(boolean enabled) {
        log.debug("Setting enabled: {}", enabled);
        this.enabled = enabled;
    }

    /**
     * Provides a default implementation for the name property. It's unlikely
     * developers will need to override.
     */
    @Override
    public String getName() {
        NamePanel namePanel = getNamePanel();
        // Check is mandatory because some LAFs (Nimbus) call getName() from
        // super constructor (so can happen before namePanel field is initialized)
        if (namePanel != null) {
            return namePanel.getName();
        }
        return ""; // $NON-NLS-1$
    }

    /**
     * Provides a default implementation for the comment property. It's unlikely
     * developers will need to override.
     *
     * @return The comment for the property
     */
    public String getComment() {
        return commentField.getText();
    }

    /**
     * Provides a default implementation for the skipped property. It's unlikely developers will need
     * to override.
     *
     * @return The comment for the property
     */
    public String getSkipped() {
        return skippedField.getText();
    }

    /**
     * Provides the Name Panel for extending classes. Extending classes are free to place it as
     * desired within the component, or not at all. Most components place the NamePanel automatically
     * by calling {@link #makeTitlePanel()} instead of directly calling this method.
     *
     * @return a NamePanel containing the name of this component
     * @deprecated use {@link #getName()} or {@link AbstractJMeterGuiComponent#createTitleLabel()} for
     * better alignment of the fields
     */
    @API(status = DEPRECATED, since = "5.2.0")
    @Deprecated
    protected NamePanel getNamePanel() {
        return namePanel;
    }

    /**
     * Provides a label containing the title for the component. Subclasses
     * typically place this label at the top of their GUI. The title is set to
     * the name returned from the component's
     * {@link JMeterGUIComponent#getStaticLabel() getStaticLabel()} method. Most
     * components place this label automatically by calling
     * {@link #makeTitlePanel()} instead of directly calling this method.
     *
     * @return a JLabel which subclasses can add to their GUI
     */
    protected Component createTitleLabel() {
        return JFactory.big(new JLabel(getStaticLabel()));
    }


    protected Component createAdvancedTitleLabel() {
        JPanel pan = new JPanel(new GridBagLayout());

        GridBagConstraints buttonConstraints = new GridBagConstraints();
        buttonConstraints.gridx = 0;
        buttonConstraints.gridy = 0;
        buttonConstraints.insets = new java.awt.Insets(0, 0, 0, 5);

        advancedButton.setFocusable(false);
        advancedButton.setPreferredSize(new java.awt.Dimension(30, 20));

        advancedButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        advancedButton.setFont(
            advancedButton.getFont()
                .deriveFont((float) advancedButton.getFont().getSize() - 4));
        advancedButton.addMouseListener(new AdvancedDisplayAction());
        advancedButton.setBackground(advancedButton.getBackground().darker());

        pan.add(advancedButton, buttonConstraints);

        JLabel titleLabel = new JLabel(getStaticLabel());
        Font curFont = titleLabel.getFont();
        titleLabel.setFont(curFont.deriveFont((float) curFont.getSize() + 4));

        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.weightx = 1.0;
        labelConstraints.fill = GridBagConstraints.HORIZONTAL;
        labelConstraints.gridx = 1;
        labelConstraints.gridy = 0;

        pan.add(titleLabel, labelConstraints);

        return pan;
    }

    /**
     * A newly created gui component can be initialized with the contents of a
     * Test Element object by calling this method. The component is responsible
     * for querying the Test Element object for the relevant information to
     * display in its GUI.
     * <p>
     * AbstractJMeterGuiComponent provides a partial implementation of this
     * method, setting the name of the component and its enabled status.
     * Subclasses should override this method, performing their own
     * configuration as needed, but also calling this super-implementation.
     *
     * @param element
     *            the TestElement to configure
     */
    @Override
    public void configure(TestElement element) {
        setName(element.getName());
        enabled = element.isEnabled();
        commentField.setText(element.getComment());
        skippedField.setText(element.getSkipped());
        if (!(element instanceof Skippable)) {
            skipPanel.setVisible(false);
        }
        hideAdvancedPanelIfEmpty();
    }

    /**
     * Provides a default implementation that resets the name field to the value of
     * getStaticLabel(), reset comment and sets enabled to true. Your GUI may need more things
     * cleared, in which case you should override, clear the extra fields, and
     * still call super.clearGui().
     */
    @Override
    public void clearGui() {
        initGui();
        enabled = true;
    }

    private void initGui() {
        setName(getStaticLabel());
        commentField.setText("");
        skippedField.setText("");
    }

    private void init() {
        initGui();
    }

    /**
     * This provides a convenience for extenders when they implement the
     * {@link JMeterGUIComponent#modifyTestElement(TestElement)} method. This
     * method will set the name, gui class, and test class for the created Test
     * Element. It should be called by every extending class when
     * creating/modifying Test Elements, as that will best assure consistent
     * behavior.
     *
     * @param mc
     *            the TestElement being created.
     */
    protected void configureTestElement(TestElement mc) {
        mc.setName(getName());

        mc.setProperty(new StringProperty(TestElement.GUI_CLASS, this.getClass().getName()));

        mc.setProperty(new StringProperty(TestElement.TEST_CLASS, mc.getClass().getName()));

        // This stores the state of the TestElement
        log.debug("setting element to enabled: {}", enabled);
        mc.setEnabled(enabled);
        mc.setComment(getComment());
        mc.setSkipped(getSkipped());
    }

    /**
     * Create a standard title section for JMeter components. This includes the
     * title for the component and the Name Panel allowing the user to change
     * the name for the component. This method is typically added to the top of
     * the component at the beginning of the component's init method.
     *
     * @return a panel containing the component title and name panel
     */
    protected Container makeTitlePanel() {
        JPanel titlePanel = new JPanel(new MigLayout("fillx, wrap 2, insets 0", "[][fill,grow]"));
        titlePanel.add(createAdvancedTitleLabel(), "span 2");

        JTextField nameField = namePanel.getNameField();
        titlePanel.add(labelFor(nameField, "name"));
        titlePanel.add(nameField);

        titlePanel.add(labelFor(nameField, "testplan_comments"));
        commentField.setWrapStyleWord(true);
        commentField.setLineWrap(true);
        titlePanel.add(commentField);
        // Note: VerticalPanel has a workaround for Box layout which aligns elements, so we can't
        // use trivial JPanel.
        // Extra wrapper is often required to ensure that further additions to the panel would be vertical
        // For instance AbstractVisualizer adds "browse file" panel
        // If it calls just ..add(browseFilePanel), then it will go to
        Container wrap = wrapTitlePanel(titlePanel);
        initAdvancedPanel();
        wrap.add(advancedPanel);
        if (isAdvancedDefault()) {
            displayAdvancedPanel();
        } else {
            hideAdvancedPanel();
        }
        return wrap;
    }


    protected boolean isAdvancedDefault() {
        return JMeterUtils.getPropDefault("jmeter.gui.advanced.display.default", true);
    }

    /**
     * Initialize advanced Panel.
     *
     * @return advanced Panel, to be used only if override.
     */
    @API(status = EXPERIMENTAL, since = "5.5.0")
    protected JPanel initAdvancedPanel() {

        advancedPanel.setName("Advanced Panel");
        advancedPanel.setBorder(BorderFactory.createTitledBorder(
            getResString("advanced_panel_title"))); // $NON-NLS-1$
        advancedPanel.setLayout(new VerticalLayout(5, VerticalLayout.BOTH));

        advancedPanel.add(skipPanel);
        skipPanel.setName("Skip Panel");
        skipPanel.add(labelFor(skippedField, "skip_label"));
        skipPanel.add(skippedField);
        skippedField.setWrapStyleWord(true);
        skippedField.setLineWrap(true);

        return advancedPanel;

    }

    @API(status = EXPERIMENTAL, since = "5.5.0")
    protected void displayAdvancedPanel() {

        advancedPanelVisible = true;
        advancedPanel.setVisible(hideAdvancedPanelIfEmpty());
        advancedButton.setText("<<");
        advancedButton.setToolTipText(getResString("advanced_hide"));

    }

    @API(status = EXPERIMENTAL, since = "5.5.0")
    protected void hideAdvancedPanel() {
        advancedPanelVisible = false;
        advancedPanel.setVisible(false);
        advancedButton.setText(">>");
        advancedButton.setToolTipText(getResString("advanced_display"));


    }

    private boolean hideAdvancedPanelIfEmpty() {
        boolean visible = false;
        for (Component component : advancedPanel.getComponents()) {
            if (component.isVisible()) {
                visible = true;
                break;
            }
        }
        advancedPanel.setVisible(visible);
        return visible;
    }

    private class AdvancedDisplayAction extends MouseAdapter {


        @Override
        public void mouseClicked(MouseEvent e) {
            int modifiers = e.getModifiersEx();
            boolean shift = ((modifiers & InputEvent.SHIFT_DOWN_MASK)
                == InputEvent.SHIFT_DOWN_MASK);
            boolean ctrl = ((modifiers & InputEvent.CTRL_DOWN_MASK) == InputEvent.CTRL_DOWN_MASK);
            boolean alt = ((modifiers & InputEvent.ALT_DOWN_MASK) == InputEvent.ALT_DOWN_MASK);

            if (e.getClickCount() == 1) {
                if (shift || ctrl || alt) {
                    GuiPackage guiPackage = GuiPackage.getInstance();
                    displayOrHide(guiPackage.getCurrentNode(), !advancedPanelVisible);

                } else {
                    if (advancedPanelVisible) {
                        hideAdvancedPanel();
                    } else {
                        displayAdvancedPanel();
                    }
                }
            }
        }

        private void displayOrHide(JMeterTreeNode node, boolean display) {
            GuiPackage guiPackage = GuiPackage.getInstance();
            JMeterGUIComponent gui = guiPackage.getGui(node.getTestElement());
            if (gui instanceof AbstractJMeterGuiComponent) {
                if (display) {
                    ((AbstractJMeterGuiComponent) gui).displayAdvancedPanel();
                } else {
                    ((AbstractJMeterGuiComponent) gui).hideAdvancedPanel();
                }
            }

            Enumeration<TreeNode> enumNode = node.children();
            while (enumNode.hasMoreElements()) {
                JMeterTreeNode child = (JMeterTreeNode) enumNode.nextElement();
                displayOrHide(child, display);
            }
        }
    }

    @API(status = EXPERIMENTAL, since = "5.2.0")
    protected Container wrapTitlePanel(Container titlePanel) {
        VerticalPanel vp = new VerticalPanel();
        vp.add(titlePanel);
        return vp;
    }

    /**
     * Create a top-level Border which can be added to JMeter components.
     * Components typically set this as their border in their init method. It
     * simply provides a nice spacing between the GUI components used and the
     * edges of the window in which they appear.
     *
     * @return a Border for JMeter components
     */
    protected Border makeBorder() {
        return BorderFactory.createEmptyBorder(10, 10, 5, 10);
    }

    /**
     * Create a scroll panel that sets it's preferred size to it's minimum size.
     * Explicitly for scroll panes that live inside other scroll panes, or
     * within containers that stretch components to fill the area they exist in.
     * Use this for any component you would put in a scroll pane (such as
     * TextAreas, tables, JLists, etc). It is here for convenience and to avoid
     * duplicate code. JMeter displays best if you follow this custom.
     *
     * @param comp
     *            the component which should be placed inside the scroll pane
     * @return a JScrollPane containing the specified component
     */
    protected JScrollPane makeScrollPane(Component comp) {
        JScrollPane pane = new JScrollPane(comp);
        pane.setPreferredSize(pane.getMinimumSize());
        return pane;
    }

    /**
     * Create a scroll panel that sets it's preferred size to it's minimum size.
     * Explicitly for scroll panes that live inside other scroll panes, or
     * within containers that stretch components to fill the area they exist in.
     * Use this for any component you would put in a scroll pane (such as
     * TextAreas, tables, JLists, etc). It is here for convenience and to avoid
     * duplicate code. JMeter displays best if you follow this custom.
     *
     * @see javax.swing.ScrollPaneConstants
     *
     * @param comp
     *            the component which should be placed inside the scroll pane
     * @param verticalPolicy
     *            the vertical scroll policy
     * @param horizontalPolicy
     *            the horizontal scroll policy
     * @return a JScrollPane containing the specified component
     */
    protected JScrollPane makeScrollPane(Component comp, int verticalPolicy, int horizontalPolicy) {
        JScrollPane pane = new JScrollPane(comp, verticalPolicy, horizontalPolicy);
        pane.setPreferredSize(pane.getMinimumSize());
        return pane;
    }

    @Override
    public String getStaticLabel() {
        return JMeterUtils.getResString(getLabelResource());
    }

    /**
     * Compute Anchor value to find reference in documentation for a particular component
     * @return String anchor
     */
    @Override
    public String getDocAnchor() {
        // Ensure we use default bundle
        String label =  JMeterUtils.getResString(getLabelResource(), new Locale("",""));
        return label.replace(' ', '_');
    }

    /**
     * Subclasses need to over-ride this method, if they wish to return
     * something other than the Visualizer itself.
     *
     * @return this object
     */
    @Override
    public JComponent getPrintableComponent() {
        return this;
    }
}
