#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>

// =================
//  13.1 幾何計算
// 　コンパイル cc -o geometric_kadai geometric_kadai.c -lm
// 　実行       ./geometric_kadai
// =================

// ------------------------
//　平面上の２点
//　　(x1, y1)、　(x2,y2)
//　の真ん中の点 (x,y) を求める
// ------------------------
void Intersection2Points(int x1, int y1, int x2, int y2)
{
    int x, y;  // 真ん中の点

    printf ("1 ２点の真ん中を求める\n");
    printf("入力点 1　：(%d, %d)\n", x1, y1);
    printf("入力点 2　：(%d, %d)\n", x2, y2);

    x = 0.0;
    y = 0.0;

    if (x1 > x2) {
        x = (x1 - x2) / 2;
    } else {
        x = (x2 - x1) / 2;
    }

    if (y1 > y2) {
        y = (y1 - y2) / 2;
    } else {
        y = (y2 - y1) / 2;
    }
    
    printf("真ん中の点：(%d, %d)\n", x, y);
}

// ------------------------
//　平面上の２点
//　　(x1, y1)、　(x2,y2)
//　を通る直線
//　　a*x + b*y + c = 0
//　を求める
// ------------------------
void LineThrough2Points(int x1, int y1, int x2, int y2)
{
    double a, b, c;

    printf ("2 ２点を通る直線を求める\n");
    printf("入力点 1：(%d,%d)\n", x1, y1);
    printf("入力点 2：(%d,%d)\n", x2, y2);

    a = 0.0;
    b = 0.0;
    c = 0.0;

    // 傾き
    a = (y2 - y1) / (x2 - x1);
    // y = ax + b
    // ax - y + b = 0
    b = y1 - a * x1;
    c = y1;

    printf("直線の方程式：%f*x + %f*y + %f = 0\n", a, b, c);
}

// ------------------------
//  足の座標
//　点(x0,y0)より
//　直線(x1,y1) - (x2,y2)に垂線を落とした時の
//　直線上の座標 (x,y) を求める
// ------------------------
void FindPoint2Line(int x0, int y0, int x1, int y1, int x2, int y2)
{
    double x, y;
    
    printf ("3 ２足の座標を求める\n");
    printf("点　：(%d,%d)\n", x0, y0);
    printf("直線：(%d,%d) - (%d,%d)\n", x1, y1, x2, y2);

    x = 0.0;    // 【実装してね】
    y = 0.0;

    printf("足の座標：(%f,%f)\n", x, y);
}

// ------------------------
//  ２直線の交点
//　２つの直線
//　　(x1,y1) - (x2,y2)
//　　(x3,y3) - (x4,y4)
//　の交点を求める
// ------------------------
void Intersection2Lines(int x1, int y1, int x2, int y2,
                        int x3, int y3, int x4, int y4)
{
    double x, y;

    printf ("4 ２直線の交点を求める\n");
    printf("直線 1：(%d,%d) - (%d,%d)\n", x1, y1, x2, y2);
    printf("直線 2：(%d,%d) - (%d,%d)\n", x3, y3, x4, y4);

    x = 0.0;    // 【実装してね】
    y = 0.0;

    printf("交点：(%f,%f)\n", x, y);
}

// ------------------------
//  sin(x)
//　テーラー展開にて sin(x)の値を求める
// ------------------------

void sinWithTaylor(double x)
{
	double sinX;

    printf ("5 テーラー展開にて sin(%f)を求める\n",x);

    sinX = 0.0;    // 【実装してね】

    printf ("sinX = %.15f\n",sinX);
}

int main()
{
    char   buff[100];
    int    x0, y0, x1, y1, x2, y2, x3, y3, x4, y4;
    double x;

    for (;;) {
        printf("> ");
        fgets(buff, sizeof(buff), stdin);
        buff[strlen(buff)-1] = '\0';
        if (buff[0] == 'E' || buff[0] == 'e') break;

        switch(buff[0]) {
        case '?':
            printf("-- 幾何計算 --\n");
            printf ("1 ２点の真ん中を求める\n");
            printf ("  Intersection2Points(int x1, int y1, int x2, int y2)\n\n");
            printf ("2 ２点を通る直線を求める\n");
            printf ("  LineThrough2Points(int x1, int y1, int x2, int y2)\n\n");
            printf ("3 ２足の座標を求める\n");
            printf ("  FindPoint2Line(int x0, int y0, int x1, int y1, int x2, int y2)\n\n");
            printf ("4 ２直線の交点を求める\n");
            printf ("  Intersection2Lines(int x1, int y1, int x2, int y2,\n");
            printf ("                     int x3, int y3, int x4, int y4)\n\n");
            printf ("5 テーラー展開にて sin(x)を求める\n");
            printf ("  sinWithTaylor(double x)\n\n");
            printf ("E → End\n\n");
           break;
        case '1':    // ２点の真ん中を求める
            sscanf(&buff[2], "%d,%d,%d,%d", &x1, &y1, &x2, &y2);

            Intersection2Points(x1, y1, x2, y2);
            break;
        case '2':    // ２点を通る直線を求める
            sscanf(&buff[2], "%d,%d,%d,%d", &x1, &y1, &x2, &y2);

            LineThrough2Points(x1, y1, x2, y2);
            break;
        case '3':    // 足の座標を求める
            sscanf(&buff[2], "%d,%d,%d,%d,%d,%d", &x0, &y0, &x1, &y1, &x2, &y2);

            FindPoint2Line(x0, y0, x1, y1, x2, y2);
            break;
        case '4':    // ２直線の交点を求める
            sscanf(&buff[2], "%d,%d,%d,%d,%d,%d,%d,%d", &x1, &y1, &x2, &y2,
                                                        &x3, &y3, &x4, &y4);

            Intersection2Lines(x1, y1, x2, y2, x3, y3, x4, y4);
            break;
        case '5':    // テーラー展開にて sin(x)を求める
            sscanf(&buff[2], "%lf", &x);

            sinWithTaylor(x);
            break;
        }
    }


    return 0;
}
