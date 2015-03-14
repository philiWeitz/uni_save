using System.Windows;
using log4net.Config;

namespace MoveObjectWpf
{
    /// <summary>
    /// Interaction logic for App.xaml
    /// </summary>
    public partial class App : Application
    {
        public App()
        {
            BasicConfigurator.Configure();
        }
    }
}