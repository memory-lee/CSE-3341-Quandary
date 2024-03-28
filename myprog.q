mutable Q main(int arg) {
    mutable Ref res = nil;
    if (arg == 1)
        res = question1();
    
    if (arg == 2)
        res = question2();

    return nil;
}


mutable Ref question1() {
    mutable Ref r1 = 1 . 1;
    mutable Ref r2 = 1 . 1;
    mutable Ref r3 = 1 . 1;
    mutable Ref r4 = 1 . 1;
    mutable Ref r5 = 1 . 1;
    mutable Ref r6 = 1 . 1;
    mutable Ref r7 = 1 . 1;
    mutable Ref r8 = 1 . 1;
    mutable Ref r9 = 1 . 1;
    mutable Ref r10 = 1 . 1;
    mutable Ref r11 = 1 . 1;
    mutable Ref r12 = 1 . 1;
    mutable Ref r13 = 1 . 1;
    mutable Ref r14 = 1 . 1;
    mutable Ref r15 = 1 . 1;
    mutable Ref r16 = 1 . 1;
    mutable Ref r17 = 1 . 1;
    return r1;
}


mutable Ref question2() {
    /* create a cyclic reference here so that RefCount can't free the dead objects */
    mutable Ref a = 1 . nil;
    mutable Ref b = 1 . a;
    setRight(a, b);
    a = nil;
    b = nil;

    Ref r1 = 1 . 1;
    Ref r2 = 1 . 1;
    Ref r3 = 1 . 1;
    Ref r4 = 1 . 1;
    Ref r5 = 1 . 1;
    Ref r6 = 1 . 1;
    Ref r7 = 1 . 1;
    Ref r8 = 1 . 1;
    Ref r9 = 1 . 1;
    Ref r10 = 1 . 1;
    Ref r11 = 1 . 1;
    Ref r12 = 1 . 1;
    Ref r13 = 1 . 1;
    Ref r14 = 1 . 1;
    Ref r15 = 1 . 1;
    Ref r16 = 1 . 1;
    return r1; 
}

