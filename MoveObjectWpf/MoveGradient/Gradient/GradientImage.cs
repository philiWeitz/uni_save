using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MoveGradient.Gradient
{
    class GradientPixel
    {
        public int Left { get; set; }
        public int Right { get; set; }
        public int Top { get; set; }
        public int Bottom { get; set; }

        public GradientPixel()
        {
            Left = 0;
            Right = 0;
            Top = 0;
            Bottom = 0;
        }
    }

    class GradientImage
    {
        public int Width { get; private set; }
        public int Height { get; private set; }


        private IList<IList<GradientPixel>> gradientImg;


        public GradientImage(int widht, int height)
        {
            initComponents(widht, height);
        }

        public GradientPixel getPixel(int x, int y)
        {
            return gradientImg[y][x];
        }

        private void initComponents(int width, int height)
        {
            Width = width;
            Height = height;

            gradientImg = new List<IList<GradientPixel>>();

            for (int row = 0; row < height; ++row)
            {
                IList<GradientPixel> gardientRow = new List<GradientPixel>();
                gradientImg.Add(gardientRow);

                for (int col = 0; col < width; ++col)
                {
                    gardientRow.Add(new GradientPixel());
                }
            }
        }
    }
}
