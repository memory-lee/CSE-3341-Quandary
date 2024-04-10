Q main(int arg) {
    Ref x = 3 . nil;
    mutable Ref y = x . 5;
    free y;
    Ref z = x . nil;
    return nil;
}