/*
* Copyright (c) 2002 and later by MH Software-Entwicklung. All Rights Reserved.
*  
* JTattoo is multiple licensed. If your are an open source developer you can use
* it under the terms and conditions of the GNU General Public License version 2.0
* or later as published by the Free Software Foundation.
*  
* see: gpl-2.0.txt
* 
* If you pay for a license you will become a registered user who could use the
* software under the terms and conditions of the GNU Lesser General Public License
* version 2.0 or later with classpath exception as published by the Free Software
* Foundation.
* 
* see: lgpl-2.0.txt
* see: classpath-exception.txt
* 
* Registered users could also use JTattoo under the terms and conditions of the 
* Apache License, Version 2.0 as published by the Apache Software Foundation.
*  
* see: APACHE-LICENSE-2.0.txt
*/
 
package com.jtattoo.plaf.mcwin;

import com.jtattoo.plaf.*;
import java.awt.*;
import javax.swing.Icon;
import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;

/**
 * @author Michael Hagen
 */
public class McWinInternalFrameTitlePane extends BaseInternalFrameTitlePane {

    public McWinInternalFrameTitlePane(JInternalFrame f) {
        super(f);
    }

    protected LayoutManager createLayout() {
        return new MyTitlePaneLayout();
    }

    public void paintBorder(Graphics g) {
        if (JTattooUtilities.isActive(this)) {
            g.setColor(AbstractLookAndFeel.getWindowBorderColor());
        } else {
            g.setColor(AbstractLookAndFeel.getWindowInactiveBorderColor());
        }
        g.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
    }

    public void paintText(Graphics g, int x, int y, String title) {
        if (isMacStyleWindowDecoration()) {
            x += paintIcon(g, x, y) + 5;
        }
        if (isActive()) {
            g.setColor(ColorHelper.brighter(AbstractLookAndFeel.getWindowTitleColorLight(), 40));
            JTattooUtilities.drawString(frame, g, title, x, y - 1);
            g.setColor(AbstractLookAndFeel.getWindowTitleForegroundColor());
            JTattooUtilities.drawString(frame, g, title, x, y - 2);
        } else {
            g.setColor(ColorHelper.brighter(AbstractLookAndFeel.getWindowInactiveTitleColorLight(), 40));
            JTattooUtilities.drawString(frame, g, title, x, y - 1);
            g.setColor(AbstractLookAndFeel.getWindowInactiveTitleForegroundColor());
            JTattooUtilities.drawString(frame, g, title, x, y - 2);
        }
    }

//------------------------------------------------------------------------------
// inner classes
//------------------------------------------------------------------------------
    class MyTitlePaneLayout extends TitlePaneLayout {

        public void addLayoutComponent(String name, Component c) {
        }

        public void removeLayoutComponent(Component c) {
        }

        public Dimension preferredLayoutSize(Container c) {
            return minimumLayoutSize(c);
        }

        public Dimension minimumLayoutSize(Container c) {
            int width = 30;
            if (frame.isClosable()) {
                width += 21;
            }
            if (frame.isMaximizable()) {
                width += 16 + (frame.isClosable() ? 10 : 4);
            }
            if (frame.isIconifiable()) {
                width += 16 + (frame.isMaximizable() ? 2 : (frame.isClosable() ? 10 : 4));
            }
            FontMetrics fm = getFontMetrics(getFont());
            String frameTitle = frame.getTitle();
            int title_w = frameTitle != null ? fm.stringWidth(frameTitle) : 0;
            int title_length = frameTitle != null ? frameTitle.length() : 0;

            if (title_length > 2) {
                int subtitle_w = fm.stringWidth(frame.getTitle().substring(0, 2) + "...");
                width += (title_w < subtitle_w) ? title_w : subtitle_w;
            } else {
                width += title_w;
            }

            int height = paletteTitleHeight;
            if (!isPalette) {
                int fontHeight = fm.getHeight() + 5;
                Icon icon = isMacStyleWindowDecoration() ? null : frame.getFrameIcon();
                int iconHeight = 0;
                if (icon != null) {
                    iconHeight = Math.min(icon.getIconHeight(), 18);
                }
                iconHeight += 5;
                height = Math.max(fontHeight, iconHeight);
            }
            return new Dimension(width, height);
        }

        public void layoutContainer(Container c) {
            if (AbstractLookAndFeel.getTheme().isMacStyleWindowDecorationOn()) {
                layoutMacStyle(c);
            } else {
                layoutDefault(c);
            }
        }

        public void layoutDefault(Container c) {
            boolean leftToRight = JTattooUtilities.isLeftToRight(frame);

            int spacing = getHorSpacing();
            int w = getWidth();
            int h = getHeight();

            // assumes all buttons have the same dimensions these dimensions include the borders
            int buttonHeight = h - getVerSpacing() - 1;
            int buttonWidth = buttonHeight;
            
            int x = leftToRight ? w - spacing : 0;
            int y = Math.max(0, ((h - buttonHeight) / 2) - 1);

            if (frame.isClosable()) {
                x += leftToRight ? -buttonWidth : spacing;
                closeButton.setBounds(x, y, buttonWidth, buttonHeight);
                if (!leftToRight) {
                    x += buttonWidth;
                }
            }

            if (frame.isMaximizable() && !isPalette) {
                x += leftToRight ? -spacing - buttonWidth : spacing;
                maxButton.setBounds(x, y, buttonWidth, buttonHeight);
                if (!leftToRight) {
                    x += buttonWidth;
                }
            }

            if (frame.isIconifiable() && !isPalette) {
                x += leftToRight ? -spacing - buttonWidth : spacing;
                iconButton.setBounds(x, y, buttonWidth, buttonHeight);
                if (!leftToRight) {
                    x += buttonWidth;
                }
            }

            buttonsWidth = leftToRight ? w - x : x;
            
            if (customTitlePanel != null) {
                int cpx = 0;
                int cpy = 0;
                int cpw = w;
                int cph = h;

                Icon icon = frame.getFrameIcon();
                if (icon != null) {
                    cpx = icon.getIconWidth() + 10;
                } else {
                    cpx = 5;
                }
                cpw -= cpx;
                
                if (!leftToRight) {
                    cpx += buttonsWidth;
                }
                cpw -= buttonsWidth;
                Graphics g = getGraphics();
                if (g != null) {
                    FontMetrics fm = g.getFontMetrics();
                    int tw = SwingUtilities.computeStringWidth(fm, JTattooUtilities.getClippedText(frame.getTitle(), fm, cpw));
                    if (leftToRight) {
                        cpx += tw;
                    }
                    cpw -= tw;
                }
                customTitlePanel.setBounds(cpx, cpy, cpw, cph);
            }
        }

        private void layoutMacStyle(Container c) {
            int spacing = getHorSpacing();
            int h = getHeight();

            // assumes all buttons have the same dimensions these dimensions include the borders
            int buttonHeight = h - getVerSpacing();
            int buttonWidth = buttonHeight;

            int x = 0;
            int y = 0;

            if (frame.isClosable()) {
                closeButton.setBounds(x, y, buttonWidth, buttonHeight);
                x += buttonWidth + spacing;
            }
            if (frame.isIconifiable() && !isPalette) {
                iconButton.setBounds(x, y, buttonWidth, buttonHeight);
                x += buttonWidth + spacing;
            }
            if (frame.isMaximizable() && !isPalette) {
                maxButton.setBounds(x, y, buttonWidth, buttonHeight);
                x += buttonWidth + spacing;
            }

            buttonsWidth = x;
            
            if (customTitlePanel != null) {
                int cpx = buttonsWidth + 5;
                int cpy = 0;
                int cpw = customTitlePanel.getPreferredSize().width;
                int cph = h;
                customTitlePanel.setBounds(cpx, cpy, cpw, cph);
                buttonsWidth += cpw + 5;
            }
        }

    } // end class MyTitlePaneLayout

}
