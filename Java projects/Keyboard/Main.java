package com.company;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.util.Locale;

public class Main extends JFrame {

    private static JButton escape;
    private static JButton F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12;
    private static JButton PrtSc, ScrLk, Pause;
    private static JButton underEsc;
    private static JButton b1, b2, b3, b4, b5, b6, b7, b8, b9, b0;
    private static JButton bMinus, bPlus;
    private static JButton backspace;
    private static JButton ins, home, PyUp;
    private static JButton tab;
    private static JButton q, w, bE, r, t, y, u, i, o, p, rightParenthesis1, rightParenthesis2, backSlash;
    private static JButton delete, end, pageDown;
    private static JButton caps;
    private static JButton a, s, d, f, g, h, j, k, l, semicolon, apostrophe, enter;
    private static JButton leftShift, rightShift;
    private static JButton z, x, c, v, b, n, m, comma, point, slash;
    private static JButton bUp, bDown, bLeft, bRight;
    private static JButton leftCtrl, rightCtrl;
    private static JButton windows, contextMenu;
    private static JButton leftAlt, rightAlt;
    private static JButton spaceBar, fn;
    private static JPanel panel;

    private Main() {
        setLayout(new FlowLayout(FlowLayout.LEFT));

        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.disabledText", Color.WHITE);
        UIManager.put("Button.background", Color.decode("#1b1b1c"));

        panel = new JPanel();
        panel.setFocusable(true);
        panel.requestFocusInWindow();
        panel.addKeyListener(new MyKeyListener());
        panel.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, java.util.Collections.EMPTY_SET);
        //panel.setBackground(Color.decode("#1b1b1c"));
        panel.setBackground(new Color(0, 0, 0, 0));
        getContentPane().add(panel);

        JLabel space0 = new JLabel("                                                                                                                                                                                                                                                                                                                                                                                                             ");
        add(space0);

        escape = new JButton("Esc");
        escape.setFont(escape.getFont().deriveFont(15.0f));
        escape.setEnabled(false);
        escape.setPreferredSize(new Dimension(59, 30));
        escape.setBorder(new RoundedBorder(10));
        add(escape);

        JLabel space = new JLabel("             ");
        add(space);

        F1 = new JButton("F1");
        F1.setFont(F1.getFont().deriveFont(15.0f));
        F1.setEnabled(false);
        F1.setPreferredSize(new Dimension(59, 30));
        F1.setBorder(new RoundedBorder(10));
        add(F1);

        F2 = new JButton("F2");
        F2.setFont(F2.getFont().deriveFont(15.0f));
        F2.setEnabled(false);
        F2.setPreferredSize(new Dimension(59, 30));
        F2.setBorder(new RoundedBorder(10));
        add(F2);

        F3 = new JButton("F3");
        F3.setFont(F3.getFont().deriveFont(15.0f));
        F3.setEnabled(false);
        F3.setPreferredSize(new Dimension(59, 30));
        F3.setBorder(new RoundedBorder(10));
        add(F3);

        F4 = new JButton("F4");
        F4.setFont(F4.getFont().deriveFont(15.0f));
        F4.setEnabled(false);
        F4.setPreferredSize(new Dimension(59, 30));
        F4.setBorder(new RoundedBorder(10));
        add(F4);

        JLabel space1 = new JLabel("       ");
        add(space1);

        F5 = new JButton("F5");
        F5.setFont(F5.getFont().deriveFont(15.0f));
        F5.setEnabled(false);
        F5.setPreferredSize(new Dimension(59, 30));
        F5.setBorder(new RoundedBorder(10));
        add(F5);

        F6 = new JButton("F6");
        F6.setFont(F6.getFont().deriveFont(15.0f));
        F6.setEnabled(false);
        F6.setPreferredSize(new Dimension(59, 30));
        F6.setBorder(new RoundedBorder(10));
        add(F6);

        F7 = new JButton("F7");
        F7.setFont(F7.getFont().deriveFont(15.0f));
        F7.setEnabled(false);
        F7.setPreferredSize(new Dimension(59, 30));
        F7.setBorder(new RoundedBorder(10));
        add(F7);

        F8 = new JButton("F8");
        F8.setFont(F8.getFont().deriveFont(15.0f));
        F8.setEnabled(false);
        F8.setPreferredSize(new Dimension(59, 30));
        F8.setBorder(new RoundedBorder(10));
        add(F8);

        JLabel space2 = new JLabel("       ");
        add(space2);

        F9 = new JButton("F9");
        F9.setFont(F9.getFont().deriveFont(15.0f));
        F9.setEnabled(false);
        F9.setPreferredSize(new Dimension(59, 30));
        F9.setBorder(new RoundedBorder(10));
        add(F9);

        F10 = new JButton("F10");
        F10.setFont(F10.getFont().deriveFont(15.0f));
        F10.setEnabled(false);
        F10.setPreferredSize(new Dimension(59, 30));
        F10.setBorder(new RoundedBorder(10));
        add(F10);

        F11 = new JButton("F11");
        F11.setFont(F11.getFont().deriveFont(15.0f));
        F11.setEnabled(false);
        F11.setPreferredSize(new Dimension(59, 30));
        F11.setBorder(new RoundedBorder(10));
        add(F11);

        F12 = new JButton("F12");
        F12.setFont(F12.getFont().deriveFont(15.0f));
        F12.setEnabled(false);
        F12.setPreferredSize(new Dimension(59, 30));
        F12.setBorder(new RoundedBorder(10));
        add(F12);

        JLabel space3 = new JLabel("     ");
        add(space3);

        PrtSc = new JButton("PrtSc");
        PrtSc.setFont(PrtSc.getFont().deriveFont(15.0f));
        PrtSc.setEnabled(false);
        PrtSc.doClick();
        PrtSc.setPreferredSize(new Dimension(62, 30));
        PrtSc.setBorder(new RoundedBorder(10));
        add(PrtSc);

        ScrLk = new JButton("ScrLk");
        ScrLk.setFont(ScrLk.getFont().deriveFont(15.0f));
        ScrLk.setEnabled(false);
        ScrLk.setPreferredSize(new Dimension(62, 30));
        ScrLk.setBorder(new RoundedBorder(10));
        add(ScrLk);

        Pause = new JButton("Pause");
        Pause.setFont(Pause.getFont().deriveFont(15.0f));
        Pause.setEnabled(false);
        Pause.setPreferredSize(new Dimension(65, 30));
        Pause.setBorder(new RoundedBorder(10));
        add(Pause);

        JLabel space4 = new JLabel("                                                                                                                                                                                                                                                                                                                                                                                                                    ");
        add(space4);

        underEsc = new JButton("` ~");
        underEsc.setFont(underEsc.getFont().deriveFont(15.0f));
        underEsc.setEnabled(false);
        underEsc.setPreferredSize(new Dimension(55, 30));
        underEsc.setBorder(new RoundedBorder(10));
        add(underEsc);

        b1 = new JButton("1");
        b1.setFont(b1.getFont().deriveFont(15.0f));
        b1.setEnabled(false);
        b1.setPreferredSize(new Dimension(58, 30));
        b1.setBorder(new RoundedBorder(10));
        add(b1);

        b2 = new JButton("2");
        b2.setFont(b2.getFont().deriveFont(15.0f));
        b2.setEnabled(false);
        b2.setPreferredSize(new Dimension(58, 30));
        b2.setBorder(new RoundedBorder(10));
        add(b2);

        b3 = new JButton("3");
        b3.setFont(b3.getFont().deriveFont(15.0f));
        b3.setEnabled(false);
        b3.setPreferredSize(new Dimension(58, 30));
        b3.setBorder(new RoundedBorder(10));
        add(b3);

        b4 = new JButton("4");
        b4.setFont(b4.getFont().deriveFont(15.0f));
        b4.setEnabled(false);
        b4.setPreferredSize(new Dimension(58, 30));
        b4.setBorder(new RoundedBorder(10));
        add(b4);

        b5 = new JButton("5");
        b5.setFont(b5.getFont().deriveFont(15.0f));
        b5.setEnabled(false);
        b5.setPreferredSize(new Dimension(58, 30));
        b5.setBorder(new RoundedBorder(10));
        add(b5);

        b6 = new JButton("6");
        b6.setFont(b6.getFont().deriveFont(15.0f));
        b6.setEnabled(false);
        b6.setPreferredSize(new Dimension(58, 30));
        b6.setBorder(new RoundedBorder(10));
        add(b6);

        b7 = new JButton("7");
        b7.setFont(b7.getFont().deriveFont(15.0f));
        b7.setEnabled(false);
        b7.setPreferredSize(new Dimension(58, 30));
        b7.setBorder(new RoundedBorder(10));
        add(b7);

        b8 = new JButton("8");
        b8.setFont(b8.getFont().deriveFont(15.0f));
        b8.setEnabled(false);
        b8.setPreferredSize(new Dimension(58, 30));
        b8.setBorder(new RoundedBorder(10));
        add(b8);

        b9 = new JButton("9");
        b9.setFont(b9.getFont().deriveFont(15.0f));
        b9.setEnabled(false);
        b9.setPreferredSize(new Dimension(58, 30));
        b9.setBorder(new RoundedBorder(10));
        add(b9);

        b0 = new JButton("0");
        b0.setFont(b0.getFont().deriveFont(15.0f));
        b0.setEnabled(false);
        b0.setPreferredSize(new Dimension(58, 30));
        b0.setBorder(new RoundedBorder(10));
        add(b0);

        bMinus = new JButton("-");
        bMinus.setFont(bMinus.getFont().deriveFont(15.0f));
        bMinus.setEnabled(false);
        bMinus.setPreferredSize(new Dimension(58, 30));
        bMinus.setBorder(new RoundedBorder(10));
        add(bMinus);

        bPlus = new JButton("+");
        bPlus.setFont(bPlus.getFont().deriveFont(15.0f));
        bPlus.setEnabled(false);
        bPlus.setPreferredSize(new Dimension(58, 30));
        bPlus.setBorder(new RoundedBorder(10));
        add(bPlus);

        backspace = new JButton("⟵");
        backspace.setFont(backspace.getFont().deriveFont(15.0f));
        backspace.setEnabled(false);
        backspace.setPreferredSize(new Dimension(108, 30));
        backspace.setBorder(new RoundedBorder(10));
        add(backspace);

        JLabel space5 = new JLabel("     ");
        add(space5);

        ins = new JButton("Ins");
        ins.setFont(ins.getFont().deriveFont(15.0f));
        ins.setEnabled(false);
        ins.setPreferredSize(new Dimension(62, 30));
        ins.setBorder(new RoundedBorder(10));
        add(ins);

        home = new JButton("Home");
        home.setFont(home.getFont().deriveFont(15.0f));
        home.setEnabled(false);
        home.setPreferredSize(new Dimension(62, 30));
        home.setBorder(new RoundedBorder(10));
        add(home);

        PyUp = new JButton("PyUp");
        PyUp.setFont(PyUp.getFont().deriveFont(15.0f));
        PyUp.setEnabled(false);
        PyUp.setPreferredSize(new Dimension(65, 30));
        PyUp.setBorder(new RoundedBorder(10));
        add(PyUp);

        tab = new JButton("Tab");
        tab.setFont(tab.getFont().deriveFont(15.0f));
        tab.setEnabled(false);
        tab.setPreferredSize(new Dimension(75, 30));
        tab.setBorder(new RoundedBorder(10));
        add(tab);

        q = new JButton("Q");
        q.setFont(q.getFont().deriveFont(15.0f));
        q.setEnabled(false);
        q.setPreferredSize(new Dimension(58, 30));
        q.setBorder(new RoundedBorder(10));
        add(q);

        w = new JButton("W");
        w.setFont(w.getFont().deriveFont(15.0f));
        w.setEnabled(false);
        w.setPreferredSize(new Dimension(58, 30));
        w.setBorder(new RoundedBorder(10));
        add(w);

        bE = new JButton("E");
        bE.setFont(bE.getFont().deriveFont(15.0f));
        bE.setEnabled(false);
        bE.setPreferredSize(new Dimension(58, 30));
        bE.setBorder(new RoundedBorder(10));
        add(bE);

        r = new JButton("R");
        r.setFont(r.getFont().deriveFont(15.0f));
        r.setEnabled(false);
        r.setPreferredSize(new Dimension(58, 30));
        r.setBorder(new RoundedBorder(10));
        add(r);

        t = new JButton("T");
        t.setFont(t.getFont().deriveFont(15.0f));
        t.setEnabled(false);
        t.setPreferredSize(new Dimension(58, 30));
        t.setBorder(new RoundedBorder(10));
        add(t);

        y = new JButton("Y");
        y.setFont(y.getFont().deriveFont(15.0f));
        y.setEnabled(false);
        y.setPreferredSize(new Dimension(58, 30));
        y.setBorder(new RoundedBorder(10));
        add(y);

        u = new JButton("U");
        u.setFont(u.getFont().deriveFont(15.0f));
        u.setEnabled(false);
        u.setPreferredSize(new Dimension(58, 30));
        u.setBorder(new RoundedBorder(10));
        add(u);

        i = new JButton("I");
        i.setFont(i.getFont().deriveFont(15.0f));
        i.setEnabled(false);
        i.setPreferredSize(new Dimension(58, 30));
        i.setBorder(new RoundedBorder(10));
        add(i);

        o = new JButton("O");
        o.setFont(o.getFont().deriveFont(15.0f));
        o.setEnabled(false);
        o.setPreferredSize(new Dimension(58, 30));
        o.setBorder(new RoundedBorder(10));
        add(o);

        p = new JButton("P");
        p.setFont(p.getFont().deriveFont(15.0f));
        p.setEnabled(false);
        p.setPreferredSize(new Dimension(58, 30));
        p.setBorder(new RoundedBorder(10));
        add(p);

        rightParenthesis1 = new JButton("[");
        rightParenthesis1.setFont(rightParenthesis1.getFont().deriveFont(15.0f));
        rightParenthesis1.setEnabled(false);
        rightParenthesis1.setPreferredSize(new Dimension(60, 30));
        rightParenthesis1.setBorder(new RoundedBorder(10));
        add(rightParenthesis1);

        rightParenthesis2 = new JButton("]");
        rightParenthesis2.setFont(rightParenthesis2.getFont().deriveFont(15.0f));
        rightParenthesis2.setEnabled(false);
        rightParenthesis2.setPreferredSize(new Dimension(60, 30));
        rightParenthesis2.setBorder(new RoundedBorder(10));
        add(rightParenthesis2);

        backSlash = new JButton("\\");
        backSlash.setFont(backSlash.getFont().deriveFont(15.0f));
        backSlash.setEnabled(false);
        backSlash.setPreferredSize(new Dimension(84, 30));
        backSlash.setBorder(new RoundedBorder(10));
        add(backSlash);

        JLabel space6 = new JLabel("     ");
        add(space6);

        delete = new JButton("Del");
        delete.setFont(delete.getFont().deriveFont(15.0f));
        delete.setEnabled(false);
        delete.setPreferredSize(new Dimension(62, 30));
        delete.setBorder(new RoundedBorder(10));
        add(delete);

        end = new JButton("End");
        end.setFont(end.getFont().deriveFont(15.0f));
        end.setEnabled(false);
        end.setPreferredSize(new Dimension(62, 30));
        end.setBorder(new RoundedBorder(10));
        add(end);

        pageDown = new JButton("PyDn");
        pageDown.setFont(pageDown.getFont().deriveFont(15.0f));
        pageDown.setEnabled(false);
        pageDown.setPreferredSize(new Dimension(65, 30));
        pageDown.setBorder(new RoundedBorder(10));
        add(pageDown);

        caps = new JButton("Caps");
        caps.setFont(caps.getFont().deriveFont(15.0f));
        caps.setEnabled(false);
        caps.setPreferredSize(new Dimension(85, 30));
        caps.setBorder(new RoundedBorder(10));
        add(caps);

        a = new JButton("A");
        a.setFont(a.getFont().deriveFont(15.0f));
        a.setEnabled(false);
        a.setPreferredSize(new Dimension(58, 30));
        a.setBorder(new RoundedBorder(10));
        add(a);

        s = new JButton("S");
        s.setFont(s.getFont().deriveFont(15.0f));
        s.setEnabled(false);
        s.setPreferredSize(new Dimension(58, 30));
        s.setBorder(new RoundedBorder(10));
        add(s);

        d = new JButton("D");
        d.setFont(d.getFont().deriveFont(15.0f));
        d.setEnabled(false);
        d.setPreferredSize(new Dimension(58, 30));
        d.setBorder(new RoundedBorder(10));
        add(d);

        f = new JButton("F");
        f.setFont(f.getFont().deriveFont(15.0f));
        f.setEnabled(false);
        f.setPreferredSize(new Dimension(58, 30));
        f.setBorder(new RoundedBorder(10));
        add(f);

        g = new JButton("G");
        g.setFont(g.getFont().deriveFont(15.0f));
        g.setEnabled(false);
        g.setPreferredSize(new Dimension(58, 30));
        g.setBorder(new RoundedBorder(10));
        add(g);

        h = new JButton("H");
        h.setFont(h.getFont().deriveFont(15.0f));
        h.setEnabled(false);
        h.setPreferredSize(new Dimension(58, 30));
        h.setBorder(new RoundedBorder(10));
        add(h);

        j = new JButton("J");
        j.setFont(j.getFont().deriveFont(15.0f));
        j.setEnabled(false);
        j.setPreferredSize(new Dimension(58, 30));
        j.setBorder(new RoundedBorder(10));
        add(j);

        k = new JButton("K");
        k.setFont(k.getFont().deriveFont(15.0f));
        k.setEnabled(false);
        k.setPreferredSize(new Dimension(58, 30));
        k.setBorder(new RoundedBorder(10));
        add(k);

        l = new JButton("L");
        l.setFont(l.getFont().deriveFont(15.0f));
        l.setEnabled(false);
        l.setPreferredSize(new Dimension(58, 30));
        l.setBorder(new RoundedBorder(10));
        add(l);

        semicolon = new JButton(";");
        semicolon.setFont(semicolon.getFont().deriveFont(15.0f));
        semicolon.setEnabled(false);
        semicolon.setPreferredSize(new Dimension(62, 30));
        semicolon.setBorder(new RoundedBorder(10));
        add(semicolon);

        apostrophe = new JButton("'");
        apostrophe.setFont(apostrophe.getFont().deriveFont(15.0f));
        apostrophe.setEnabled(false);
        apostrophe.setPreferredSize(new Dimension(62, 30));
        apostrophe.setBorder(new RoundedBorder(10));
        add(apostrophe);

        enter = new JButton("Enter");
        enter.setFont(enter.getFont().deriveFont(15.0f));
        enter.setEnabled(false);
        enter.setPreferredSize(new Dimension(133, 30));
        enter.setBorder(new RoundedBorder(10));
        add(enter);

        JLabel space7 = new JLabel("                                                ");
        add(space7);

        leftShift = new JButton("Shift");
        leftShift.setFont(leftShift.getFont().deriveFont(15.0f));
        leftShift.setEnabled(false);
        leftShift.setPreferredSize(new Dimension(120, 30));
        leftShift.setBorder(new RoundedBorder(10));
        add(leftShift);

        z = new JButton("Z");
        z.setFont(z.getFont().deriveFont(15.0f));
        z.setEnabled(false);
        z.setPreferredSize(new Dimension(58, 30));
        z.setBorder(new RoundedBorder(10));
        add(z);

        x = new JButton("X");
        x.setFont(x.getFont().deriveFont(15.0f));
        x.setEnabled(false);
        x.setPreferredSize(new Dimension(58, 30));
        x.setBorder(new RoundedBorder(10));
        add(x);

        c = new JButton("C");
        c.setFont(c.getFont().deriveFont(15.0f));
        c.setEnabled(false);
        c.setPreferredSize(new Dimension(58, 30));
        c.setBorder(new RoundedBorder(10));
        add(c);

        v = new JButton("V");
        v.setFont(v.getFont().deriveFont(15.0f));
        v.setEnabled(false);
        v.setPreferredSize(new Dimension(58, 30));
        v.setBorder(new RoundedBorder(10));
        add(v);

        b = new JButton("B");
        b.setFont(b.getFont().deriveFont(15.0f));
        b.setEnabled(false);
        b.setPreferredSize(new Dimension(58, 30));
        b.setBorder(new RoundedBorder(10));
        add(b);

        n = new JButton("N");
        n.setFont(n.getFont().deriveFont(15.0f));
        n.setEnabled(false);
        n.setPreferredSize(new Dimension(58, 30));
        n.setBorder(new RoundedBorder(10));
        add(n);

        m = new JButton("M");
        m.setFont(m.getFont().deriveFont(15.0f));
        m.setEnabled(false);
        m.setPreferredSize(new Dimension(58, 30));
        m.setBorder(new RoundedBorder(10));
        add(m);

        comma = new JButton(",");
        comma.setFont(comma.getFont().deriveFont(15.0f));
        comma.setEnabled(false);
        comma.setPreferredSize(new Dimension(62, 30));
        comma.setBorder(new RoundedBorder(10));
        add(comma);

        point = new JButton(".");
        point.setFont(point.getFont().deriveFont(15.0f));
        point.setEnabled(false);
        point.setPreferredSize(new Dimension(62, 30));
        point.setBorder(new RoundedBorder(10));
        add(point);

        slash = new JButton("/");
        slash.setFont(slash.getFont().deriveFont(15.0f));
        slash.setEnabled(false);
        slash.setPreferredSize(new Dimension(62, 30));
        slash.setBorder(new RoundedBorder(10));
        add(slash);

        rightShift = new JButton("Shift");
        rightShift.setFont(rightShift.getFont().deriveFont(15.0f));
        rightShift.setEnabled(false);
        rightShift.setPreferredSize(new Dimension(157, 30));
        rightShift.setBorder(new RoundedBorder(10));
        add(rightShift);

        JLabel space8 = new JLabel("                            ");
        add(space8);

        bUp = new JButton("^");
        bUp.setFont(bUp.getFont().deriveFont(15.0f));
        bUp.setEnabled(false);
        bUp.setPreferredSize(new Dimension(62, 30));
        bUp.setBorder(new RoundedBorder(10));
        add(bUp);

        leftCtrl = new JButton("Ctrl");
        leftCtrl.setFont(leftCtrl.getFont().deriveFont(15.0f));
        leftCtrl.setEnabled(false);
        leftCtrl.setPreferredSize(new Dimension(70, 30));
        leftCtrl.setBorder(new RoundedBorder(10));
        add(leftCtrl);

        windows = new JButton("Win");
        windows.setFont(windows.getFont().deriveFont(15.0f));
        windows.setEnabled(false);
        windows.setPreferredSize(new Dimension(65, 30));
        windows.setBorder(new RoundedBorder(10));
        add(windows);

        leftAlt = new JButton("Alt");
        leftAlt.setFont(leftAlt.getFont().deriveFont(15.0f));
        leftAlt.setEnabled(false);
        leftAlt.setPreferredSize(new Dimension(65, 30));
        leftAlt.setBorder(new RoundedBorder(10));
        add(leftAlt);

        spaceBar = new JButton("                                  ");
        spaceBar.setFont(spaceBar.getFont().deriveFont(15.0f));
        spaceBar.setEnabled(false);
        spaceBar.setPreferredSize(new Dimension(424, 30));
        spaceBar.setBorder(new RoundedBorder(10));
        add(spaceBar);

        rightAlt = new JButton("Alt");
        rightAlt.setFont(rightAlt.getFont().deriveFont(15.0f));
        rightAlt.setEnabled(false);
        rightAlt.setPreferredSize(new Dimension(65, 30));
        rightAlt.setBorder(new RoundedBorder(10));
        add(rightAlt);

        fn = new JButton("Fn");
        fn.setFont(fn.getFont().deriveFont(15.0f));
        fn.setEnabled(false);
        fn.setPreferredSize(new Dimension(65, 30));
        fn.setBorder(new RoundedBorder(10));
        add(fn);

        contextMenu = new JButton("Menu");
        contextMenu.setFont(contextMenu.getFont().deriveFont(15.0f));
        contextMenu.setEnabled(false);
        contextMenu.setPreferredSize(new Dimension(65, 30));
        contextMenu.setBorder(new RoundedBorder(10));
        add(contextMenu);

        rightCtrl = new JButton("Ctrl");
        rightCtrl.setFont(rightCtrl.getFont().deriveFont(15.0f));
        rightCtrl.setEnabled(false);
        rightCtrl.setPreferredSize(new Dimension(70, 30));
        rightCtrl.setBorder(new RoundedBorder(10));
        add(rightCtrl);

        JLabel space9 = new JLabel("     ");
        add(space9);

        bLeft = new JButton("<");
        bLeft.setFont(bLeft.getFont().deriveFont(15.0f));
        bLeft.setEnabled(false);
        bLeft.setPreferredSize(new Dimension(63, 30));
        bLeft.setBorder(new RoundedBorder(10));
        add(bLeft);

        bDown = new JButton("v");
        bDown.setFont(bDown.getFont().deriveFont(15.0f));
        bDown.setEnabled(false);
        bDown.setPreferredSize(new Dimension(64, 30));
        bDown.setBorder(new RoundedBorder(10));
        add(bDown);

        bRight = new JButton(">");
        bRight.setFont(bRight.getFont().deriveFont(15.0f));
        bRight.setEnabled(false);
        bRight.setPreferredSize(new Dimension(63, 30));
        bRight.setBorder(new RoundedBorder(10));
        add(bRight);
    }

    public static void main(String[] args) {
	    Main gui = new Main();
	    gui.setResizable(false);
	    gui.setVisible(true);
	    gui.getContentPane().setBackground(Color.decode("#1b1b1c"));
	    gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    gui.setTitle("Keyboard tester");
	    gui.setSize(1175, 320);
        gui.setLocale(Locale.ENGLISH);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = screenSize.height;
        int width = screenSize.width;
        gui.setLocation(width / 2 - gui.getSize().width / 2, height / 2 - gui.getSize().height / 2);

        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * tab = true;
     * print screen = false;
     * fn = false;
     *
     */

    static class MyKeyListener extends KeyAdapter {
        @Override
        public void keyTyped(KeyEvent e) {/* nothing */}

        @Override
        public void keyPressed(KeyEvent e){
            System.out.println(KeyEvent.getKeyText(e.getKeyCode()));
            UIManager.put("Button.disabledText", Color.decode("#34eb34"));
            if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                escape.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                escape.setForeground(Color.decode("#34eb34"));
                escape.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_F1) {
                F1.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                F1.setForeground(Color.decode("#34eb34"));
                F1.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_F2) {
                F2.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                F2.setForeground(Color.decode("#34eb34"));
                F2.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_F3) {
                F3.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                F3.setForeground(Color.decode("#34eb34"));
                F3.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_F4) {
                F4.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                F4.setForeground(Color.decode("#34eb34"));
                F4.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_F5) {
                F5.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                F5.setForeground(Color.decode("#34eb34"));
                F5.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_F6) {
                F6.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                F6.setForeground(Color.decode("#34eb34"));
                F6.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_F7) {
                F7.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                F7.setForeground(Color.decode("#34eb34"));
                F7.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_F8) {
                F8.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                F8.setForeground(Color.decode("#34eb34"));
                F8.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_F9) {
                F9.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                F9.setForeground(Color.decode("#34eb34"));
                F9.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_F10) {
                F10.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                F10.setForeground(Color.decode("#34eb34"));
                F10.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_F11) {
                F11.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                F11.setForeground(Color.decode("#34eb34"));
                F11.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_F12) {
                F12.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                F12.setForeground(Color.decode("#34eb34"));
                F12.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_PRINTSCREEN) {
                PrtSc.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                PrtSc.setForeground(Color.decode("#34eb34"));
                PrtSc.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_SCROLL_LOCK) {
                ScrLk.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                ScrLk.setForeground(Color.decode("#34eb34"));
                ScrLk.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_PAUSE) {
                Pause.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                Pause.setForeground(Color.decode("#34eb34"));
                Pause.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_BACK_QUOTE) {
                underEsc.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                underEsc.setForeground(Color.decode("#34eb34"));
                underEsc.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_1) {
                b1.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                b1.setForeground(Color.decode("#34eb34"));
                b1.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_2) {
                b2.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                b2.setForeground(Color.decode("#34eb34"));
                b2.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_3) {
                b3.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                b3.setForeground(Color.decode("#34eb34"));
                b3.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_4) {
                b4.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                b4.setForeground(Color.decode("#34eb34"));
                b4.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_5) {
                b5.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                b5.setForeground(Color.decode("#34eb34"));
                b5.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_6) {
                b6.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                b6.setForeground(Color.decode("#34eb34"));
                b6.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_7) {
                b7.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                b7.setForeground(Color.decode("#34eb34"));
                b7.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_8) {
                b8.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                b8.setForeground(Color.decode("#34eb34"));
                b2.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_9) {
                b9.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                b9.setForeground(Color.decode("#34eb34"));
                b9.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_0) {
                b0.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                b0.setForeground(Color.decode("#34eb34"));
                b0.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_MINUS) {
                bMinus.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                bMinus.setForeground(Color.decode("#34eb34"));
                bMinus.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_EQUALS) {
                bPlus.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                bPlus.setForeground(Color.decode("#34eb34"));
                bPlus.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                backspace.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                backspace.setForeground(Color.decode("#34eb34"));
                backspace.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_INSERT) {
                ins.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                ins.setForeground(Color.decode("#34eb34"));
                ins.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_HOME) {
                home.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                home.setForeground(Color.decode("#34eb34"));
                home.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_PAGE_UP) {
                PyUp.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                PyUp.setForeground(Color.decode("#34eb34"));
                PyUp.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_TAB) {
                tab.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                tab.setForeground(Color.decode("#34eb34"));
                tab.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_Q) {
                q.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                q.setForeground(Color.decode("#34eb34"));
                q.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_W) {
                w.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                w.setForeground(Color.decode("#34eb34"));
                w.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_E) {
                bE.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                bE.setForeground(Color.decode("#34eb34"));
                bE.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_R) {
                r.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                r.setForeground(Color.decode("#34eb34"));
                r.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_T) {
                t.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                t.setForeground(Color.decode("#34eb34"));
                t.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_Y) {
                y.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                y.setForeground(Color.decode("#34eb34"));
                y.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_U) {
                u.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                u.setForeground(Color.decode("#34eb34"));
                u.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_I) {
                i.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                i.setForeground(Color.decode("#34eb34"));
                i.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_O) {
                o.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                o.setForeground(Color.decode("#34eb34"));
                o.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_P) {
                p.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                p.setForeground(Color.decode("#34eb34"));
                p.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_OPEN_BRACKET) {
                rightParenthesis1.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                rightParenthesis1.setForeground(Color.decode("#34eb34"));
                rightParenthesis1.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_CLOSE_BRACKET) {
                rightParenthesis2.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                rightParenthesis2.setForeground(Color.decode("#34eb34"));
                rightParenthesis2.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_BACK_SLASH) {
                backSlash.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                backSlash.setForeground(Color.decode("#34eb34"));
                backSlash.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_DELETE) {
                delete.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                delete.setForeground(Color.decode("#34eb34"));
                delete.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_END) {
                end.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                end.setForeground(Color.decode("#34eb34"));
                end.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
                pageDown.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                pageDown.setForeground(Color.decode("#34eb34"));
                pageDown.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_CAPS_LOCK) {
                caps.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                caps.setForeground(Color.decode("#34eb34"));
                caps.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_A) {
                a.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                a.setForeground(Color.decode("#34eb34"));
                a.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_S) {
                s.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                s.setForeground(Color.decode("#34eb34"));
                s.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_D) {
                d.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                d.setForeground(Color.decode("#34eb34"));
                d.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_F) {
                f.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                f.setForeground(Color.decode("#34eb34"));
                f.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_G) {
                g.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                g.setForeground(Color.decode("#34eb34"));
                g.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_H) {
                h.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                h.setForeground(Color.decode("#34eb34"));
                h.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_J) {
                j.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                j.setForeground(Color.decode("#34eb34"));
                j.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_K) {
                k.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                k.setForeground(Color.decode("#34eb34"));
                k.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_L) {
                l.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                l.setForeground(Color.decode("#34eb34"));
                l.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_SEMICOLON) {
                semicolon.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                semicolon.setForeground(Color.decode("#34eb34"));
                semicolon.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_QUOTE) {
                apostrophe.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                apostrophe.setForeground(Color.decode("#34eb34"));
                apostrophe.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                enter.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                enter.setForeground(Color.decode("#34eb34"));
                enter.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_SHIFT) {
                leftShift.setFont(new Font("Sans Serif", Font.ITALIC, 15 - 1 + 1));
                leftShift.setForeground(Color.decode("#34eb34"));
                leftShift.setBorder(new RoundedBorder(10));
                rightShift.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                rightShift.setForeground(Color.decode("#34eb34"));
                rightShift.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_Z) {
                z.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                z.setForeground(Color.decode("#34eb34"));
                z.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_X) {
                x.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                x.setForeground(Color.decode("#34eb34"));
                x.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_C) {
                c.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                c.setForeground(Color.decode("#34eb34"));
                c.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_V) {
                v.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                v.setForeground(Color.decode("#34eb34"));
                v.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_B) {
                b.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                b.setForeground(Color.decode("#34eb34"));
                b.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_N) {
                n.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                n.setForeground(Color.decode("#34eb34"));
                n.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_M) {
                m.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                m.setForeground(Color.decode("#34eb34"));
                m.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_COMMA) {
                comma.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                comma.setForeground(Color.decode("#34eb34"));
                comma.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_PERIOD) {
                point.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                point.setForeground(Color.decode("#34eb34"));
                point.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_SLASH) {
                slash.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                slash.setForeground(Color.decode("#34eb34"));
                slash.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_UP) {
                bUp.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                bUp.setForeground(Color.decode("#34eb34"));
                bUp.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_CONTROL) {
                leftCtrl.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                leftCtrl.setForeground(Color.decode("#34eb34"));
                leftCtrl.setBorder(new RoundedBorder(10));
                rightCtrl.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                rightCtrl.setForeground(Color.decode("#34eb34"));
                rightCtrl.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_WINDOWS) {
                windows.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                windows.setForeground(Color.decode("#34eb34"));
                windows.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_ALT) {
                leftAlt.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                leftAlt.setForeground(Color.decode("#34eb34"));
                leftAlt.setBorder(new RoundedBorder(10));
                rightAlt.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                rightAlt.setForeground(Color.decode("#34eb34"));
                rightAlt.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_SPACE) {
                spaceBar.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                spaceBar.setForeground(Color.decode("#34eb34"));
                spaceBar.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_F13) {
                fn.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                fn.setForeground(Color.decode("#34eb34"));
                fn.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_CONTEXT_MENU) {
                contextMenu.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                contextMenu.setForeground(Color.decode("#34eb34"));
                contextMenu.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_LEFT) {
                bLeft.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                bLeft.setForeground(Color.decode("#34eb34"));
                bLeft.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
                bDown.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                bDown.setForeground(Color.decode("#34eb34"));
                bDown.setBorder(new RoundedBorder(10));
            } else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
                bRight.setFont(new Font("Sans Serif", Font.ITALIC, 15));
                bRight.setForeground(Color.decode("#34eb34"));
                bRight.setBorder(new RoundedBorder(10));
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            UIManager.put("Button.disabledText", Color.WHITE);
            if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                escape.setFont(new Font("Sans Serif", Font.BOLD, 15));
                escape.setBackground(Color.decode("#1b1b1c"));
                escape.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_F1) {
                F1.setFont(new Font("Sans Serif", Font.BOLD, 15));
                F1.setBackground(Color.decode("#1b1b1c"));
                F1.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_F2) {
                F2.setFont(new Font("Sans Serif", Font.BOLD, 15));
                F2.setBackground(Color.decode("#1b1b1c"));
                F2.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_F3) {
                F3.setFont(new Font("Sans Serif", Font.BOLD, 15));
                F3.setBackground(Color.decode("#1b1b1c"));
                F3.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_F4) {
                F4.setFont(new Font("Sans Serif", Font.BOLD, 15));
                F4.setBackground(Color.decode("#1b1b1c"));
                F4.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_F5) {
                F5.setFont(new Font("Sans Serif", Font.BOLD, 15));
                F5.setBackground(Color.decode("#1b1b1c"));
                F5.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_F6) {
                F6.setFont(new Font("Sans Serif", Font.BOLD, 15));
                F6.setBackground(Color.decode("#1b1b1c"));
                F6.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_F7) {
                F7.setFont(new Font("Sans Serif", Font.BOLD, 15));
                F7.setBackground(Color.decode("#1b1b1c"));
                F7.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_F8) {
                F8.setFont(new Font("Sans Serif", Font.BOLD, 15));
                F8.setBackground(Color.decode("#1b1b1c"));
                F8.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_F9) {
                F9.setFont(new Font("Sans Serif", Font.BOLD, 15));
                F9.setBackground(Color.decode("#1b1b1c"));
                F9.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_F10) {
                F10.setFont(new Font("Sans Serif", Font.BOLD, 15));
                F10.setBackground(Color.decode("#1b1b1c"));
                F10.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_F11) {
                F11.setFont(new Font("Sans Serif", Font.BOLD, 15));
                F11.setBackground(Color.decode("#1b1b1c"));
                F11.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_F12) {
                F12.setFont(new Font("Sans Serif", Font.BOLD, 15));
                F12.setBackground(Color.decode("#1b1b1c"));
                F12.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_PRINTSCREEN) {
                PrtSc.setFont(new Font("Sans Serif", Font.BOLD, 15));
                PrtSc.setBackground(Color.decode("#1b1b1c"));
                PrtSc.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_SCROLL_LOCK) {
                ScrLk.setFont(new Font("Sans Serif", Font.BOLD, 15));
                ScrLk.setBackground(Color.decode("#1b1b1c"));
                ScrLk.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_PAUSE) {
                Pause.setFont(new Font("Sans Serif", Font.BOLD, 15));
                Pause.setBackground(Color.decode("#1b1b1c"));
                Pause.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_BACK_QUOTE) {
                underEsc.setFont(new Font("Sans Serif", Font.BOLD, 15));
                underEsc.setBackground(Color.decode("#1b1b1c"));
                underEsc.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_1) {
                b1.setFont(new Font("Sans Serif", Font.BOLD, 15));
                b1.setBackground(Color.decode("#1b1b1c"));
                b1.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_2) {
                b2.setFont(new Font("Sans Serif", Font.BOLD, 15));
                b2.setBackground(Color.decode("#1b1b1c"));
                b2.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_3) {
                b3.setFont(new Font("Sans Serif", Font.BOLD, 15));
                b3.setBackground(Color.decode("#1b1b1c"));
                b3.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_4) {
                b4.setFont(new Font("Sans Serif", Font.BOLD, 15));
                b4.setBackground(Color.decode("#1b1b1c"));
                b4.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_5) {
                b5.setFont(new Font("Sans Serif", Font.BOLD, 15));
                b5.setBackground(Color.decode("#1b1b1c"));
                b5.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_6) {
                b6.setFont(new Font("Sans Serif", Font.BOLD, 15));
                b6.setBackground(Color.decode("#1b1b1c"));
                b6.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_7) {
                b7.setFont(new Font("Sans Serif", Font.BOLD, 15));
                b7.setBackground(Color.decode("#1b1b1c"));
                b7.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_8) {
                b8.setFont(new Font("Sans Serif", Font.BOLD, 15));
                b8.setBackground(Color.decode("#1b1b1c"));
                b8.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_9) {
                b9.setFont(new Font("Sans Serif", Font.BOLD, 15));
                b9.setBackground(Color.decode("#1b1b1c"));
                b9.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_0) {
                b0.setFont(new Font("Sans Serif", Font.BOLD, 15));
                b0.setBackground(Color.decode("#1b1b1c"));
                b0.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_MINUS) {
                bMinus.setFont(new Font("Sans Serif", Font.BOLD, 15));
                bMinus.setBackground(Color.decode("#1b1b1c"));
                bMinus.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_EQUALS) {
                bPlus.setFont(new Font("Sans Serif", Font.BOLD, 15));
                bPlus.setBackground(Color.decode("#1b1b1c"));
                bPlus.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                backspace.setFont(new Font("Sans Serif", Font.BOLD, 15));
                backspace.setBackground(Color.decode("#1b1b1c"));
                backspace.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_INSERT) {
                ins.setFont(new Font("Sans Serif", Font.BOLD, 15));
                ins.setBackground(Color.decode("#1b1b1c"));
                ins.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_HOME) {
                home.setFont(new Font("Sans Serif", Font.BOLD, 15));
                home.setBackground(Color.decode("#1b1b1c"));
                home.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_TAB) {
                tab.setFont(new Font("Sans Serif", Font.BOLD, 15));
                tab.setBackground(Color.decode("#1b1b1c"));
                tab.setForeground(Color.WHITE);
                panel.grabFocus();
            } else if(e.getKeyCode() == KeyEvent.VK_Q) {
                q.setFont(new Font("Sans Serif", Font.BOLD, 15));
                q.setBackground(Color.decode("#1b1b1c"));
                q.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_W) {
                w.setFont(new Font("Sans Serif", Font.BOLD, 15));
                w.setBackground(Color.decode("#1b1b1c"));
                w.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_E) {
                bE.setFont(new Font("Sans Serif", Font.BOLD, 15));
                bE.setBackground(Color.decode("#1b1b1c"));
                bE.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_R) {
                r.setFont(new Font("Sans Serif", Font.BOLD, 15));
                r.setBackground(Color.decode("#1b1b1c"));
                r.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_T) {
                t.setFont(new Font("Sans Serif", Font.BOLD, 15));
                t.setBackground(Color.decode("#1b1b1c"));
                t.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_Y) {
                y.setFont(new Font("Sans Serif", Font.BOLD, 15));
                y.setBackground(Color.decode("#1b1b1c"));
                y.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_U) {
                u.setFont(new Font("Sans Serif", Font.BOLD, 15));
                u.setBackground(Color.decode("#1b1b1c"));
                u.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_I) {
                i.setFont(new Font("Sans Serif", Font.BOLD, 15));
                i.setBackground(Color.decode("#1b1b1c"));
                i.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_O) {
                o.setFont(new Font("Sans Serif", Font.BOLD, 15));
                o.setBackground(Color.decode("#1b1b1c"));
                o.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_P) {
                p.setFont(new Font("Sans Serif", Font.BOLD, 15));
                p.setBackground(Color.decode("#1b1b1c"));
                p.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_OPEN_BRACKET) {
                rightParenthesis1.setFont(new Font("Sans Serif", Font.BOLD, 15));
                rightParenthesis1.setBackground(Color.decode("#1b1b1c"));
                rightParenthesis1.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_CLOSE_BRACKET) {
                rightParenthesis2.setFont(new Font("Sans Serif", Font.BOLD, 15));
                rightParenthesis2.setBackground(Color.decode("#1b1b1c"));
                rightParenthesis2.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_BACK_SLASH) {
                backSlash.setFont(new Font("Sans Serif", Font.BOLD, 15));
                backSlash.setBackground(Color.decode("#1b1b1c"));
                backSlash.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_DELETE) {
                delete.setFont(new Font("Sans Serif", Font.BOLD, 15));
                delete.setBackground(Color.decode("#1b1b1c"));
                delete.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_END) {
                end.setFont(new Font("Sans Serif", Font.BOLD, 15));
                end.setBackground(Color.decode("#1b1b1c"));
                end.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
                pageDown.setFont(new Font("Sans Serif", Font.BOLD, 15));
                pageDown.setBackground(Color.decode("#1b1b1c"));
                pageDown.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_CAPS_LOCK) {
                caps.setFont(new Font("Sans Serif", Font.BOLD, 15));
                caps.setBackground(Color.decode("#1b1b1c"));
                caps.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_A) {
                a.setFont(new Font("Sans Serif", Font.BOLD, 15));
                a.setBackground(Color.decode("#1b1b1c"));
                a.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_S) {
                s.setFont(new Font("Sans Serif", Font.BOLD, 15));
                s.setBackground(Color.decode("#1b1b1c"));
                s.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_D) {
                d.setFont(new Font("Sans Serif", Font.BOLD, 15));
                d.setBackground(Color.decode("#1b1b1c"));
                d.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_F) {
                f.setFont(new Font("Sans Serif", Font.BOLD, 15));
                f.setBackground(Color.decode("#1b1b1c"));
                f.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_G) {
                g.setFont(new Font("Sans Serif", Font.BOLD, 15));
                g.setBackground(Color.decode("#1b1b1c"));
                g.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_H) {
                h.setFont(new Font("Sans Serif", Font.BOLD, 15));
                h.setBackground(Color.decode("#1b1b1c"));
                h.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_J) {
                j.setFont(new Font("Sans Serif", Font.BOLD, 15));
                j.setBackground(Color.decode("#1b1b1c"));
                j.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_K) {
                k.setFont(new Font("Sans Serif", Font.BOLD, 15));
                k.setBackground(Color.decode("#1b1b1c"));
                k.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_L) {
                l.setFont(new Font("Sans Serif", Font.BOLD, 15));
                l.setBackground(Color.decode("#1b1b1c"));
                l.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_SEMICOLON) {
                semicolon.setFont(new Font("Sans Serif", Font.BOLD, 15));
                semicolon.setBackground(Color.decode("#1b1b1c"));
                semicolon.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_QUOTE) {
                apostrophe.setFont(new Font("Sans Serif", Font.BOLD, 15));
                apostrophe.setBackground(Color.decode("#1b1b1c"));
                apostrophe.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                enter.setFont(new Font("Sans Serif", Font.BOLD, 15));
                enter.setBackground(Color.decode("#1b1b1c"));
                enter.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_SHIFT) {
                leftShift.setFont(new Font("Sans Serif", Font.BOLD, 15 - 1 + 1));
                leftShift.setBackground(Color.decode("#1b1b1c"));
                leftShift.setForeground(Color.WHITE);
                rightShift.setFont(new Font("Sans Serif", Font.BOLD, 15));
                rightShift.setBackground(Color.decode("#1b1b1c"));
                rightShift.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_Z) {
                z.setFont(new Font("Sans Serif", Font.BOLD, 15));
                z.setBackground(Color.decode("#1b1b1c"));
                z.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_X) {
                x.setFont(new Font("Sans Serif", Font.BOLD, 15));
                x.setBackground(Color.decode("#1b1b1c"));
                x.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_C) {
                c.setFont(new Font("Sans Serif", Font.BOLD, 15));
                c.setBackground(Color.decode("#1b1b1c"));
                c.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_V) {
                v.setFont(new Font("Sans Serif", Font.BOLD, 15));
                v.setBackground(Color.decode("#1b1b1c"));
                v.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_B) {
                b.setFont(new Font("Sans Serif", Font.BOLD, 15));
                b.setBackground(Color.decode("#1b1b1c"));
                b.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_N) {
                n.setFont(new Font("Sans Serif", Font.BOLD, 15));
                n.setBackground(Color.decode("#1b1b1c"));
                n.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_M) {
                m.setFont(new Font("Sans Serif", Font.BOLD, 15));
                m.setBackground(Color.decode("#1b1b1c"));
                m.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_COMMA) {
                comma.setFont(new Font("Sans Serif", Font.BOLD, 15));
                comma.setBackground(Color.decode("#1b1b1c"));
                comma.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_PERIOD) {
                point.setFont(new Font("Sans Serif", Font.BOLD, 15));
                point.setBackground(Color.decode("#1b1b1c"));
                point.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_SLASH) {
                slash.setFont(new Font("Sans Serif", Font.BOLD, 15));
                slash.setBackground(Color.decode("#1b1b1c"));
                slash.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_UP) {
                bUp.setFont(new Font("Sans Serif", Font.BOLD, 15));
                bUp.setBackground(Color.decode("#1b1b1c"));
                bUp.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_CONTROL) {
                leftCtrl.setFont(new Font("Sans Serif", Font.BOLD, 15));
                leftCtrl.setBackground(Color.decode("#1b1b1c"));
                leftCtrl.setForeground(Color.WHITE);
                rightCtrl.setFont(new Font("Sans Serif", Font.BOLD, 15));
                rightCtrl.setBackground(Color.decode("#1b1b1c"));
                rightCtrl.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_WINDOWS) {
                windows.setFont(new Font("Sans Serif", Font.BOLD, 15));
                windows.setBackground(Color.decode("#1b1b1c"));
                windows.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_ALT) {
                leftAlt.setFont(new Font("Sans Serif", Font.BOLD, 15));
                leftAlt.setBackground(Color.decode("#1b1b1c"));
                leftAlt.setForeground(Color.WHITE);
                rightAlt.setFont(new Font("Sans Serif", Font.BOLD, 15));
                rightAlt.setBackground(Color.decode("#1b1b1c"));
                rightAlt.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_SPACE) {
                spaceBar.setFont(new Font("Sans Serif", Font.BOLD, 15));
                spaceBar.setBackground(Color.decode("#1b1b1c"));
                spaceBar.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_F13) {
                fn.setFont(new Font("Sans Serif", Font.BOLD, 15));
                fn.setBackground(Color.decode("#1b1b1c"));
                fn.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_CONTEXT_MENU) {
                contextMenu.setFont(new Font("Sans Serif", Font.BOLD, 15));
                contextMenu.setBackground(Color.decode("#1b1b1c"));
                contextMenu.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_LEFT) {
                bLeft.setFont(new Font("Sans Serif", Font.BOLD, 15));
                bLeft.setBackground(Color.decode("#1b1b1c"));
                bLeft.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
                bDown.setFont(new Font("Sans Serif", Font.BOLD, 15));
                bDown.setBackground(Color.decode("#1b1b1c"));
                bDown.setForeground(Color.WHITE);
            } else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
                bRight.setFont(new Font("Sans Serif", Font.BOLD, 15));
                bRight.setBackground(Color.decode("#1b1b1c"));
                bRight.setForeground(Color.WHITE);
            }
        }
    }

    private static class RoundedBorder implements Border {

        private int radius;

        RoundedBorder(int radius) {
            this.radius = radius;
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius + 1, this.radius + 1, this.radius + 2, this.radius);
        }

        public boolean isBorderOpaque() {
            return true;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
    }
}
