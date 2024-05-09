using Newtonsoft.Json;
using SixLabors.ImageSharp;
using SixLabors.ImageSharp.Processing;

namespace sample1;

class Program
{
    static void Main(string[] args)
    {
        // PART 1 
        Personne personne = new Personne("John Doe", 30);

        Console.WriteLine(personne.Hello(isLowercase: true));
        Console.WriteLine(personne.Hello(isLowercase: false));

        // PART 2
        Console.WriteLine(JsonConvert.SerializeObject(personne, Formatting.Indented));

        // PART 3
        string sourceFolder = @".\ig_pictures";
        string destFolder1 = @".\ig_pictures_resized_sequentially";
        string destFolder2 = @".\ig_pictures_resized_parallely";

        Directory.CreateDirectory(destFolder1);
        Directory.CreateDirectory(destFolder2);

        List<string> imagePaths = new List<string>(Directory.GetFiles(sourceFolder));

        ResizeImagesSequentially(imagePaths, destFolder1);
        ResizeImagesParallely(imagePaths, destFolder2);
    }

    static void ResizeImagesSequentially(List<string> imagePaths, string destFolder)
    {
        var watchSequential = System.Diagnostics.Stopwatch.StartNew();

        foreach (var currentImagePath in imagePaths)
        {
            using (Image image = Image.Load(currentImagePath))
            {
                image.Mutate(x => x.Resize(image.Width / 2, image.Height / 2));
                image.Save(Path.Combine(destFolder, Path.GetFileNameWithoutExtension(currentImagePath) + "_resized" + Path.GetExtension(currentImagePath)));
            }
        }

        watchSequential.Stop();
        var elapsedMsSequential = watchSequential.ElapsedMilliseconds;
        Console.WriteLine($"Images resized sequentially in {elapsedMsSequential} ms");
    }

    static void ResizeImagesParallely(List<string> imagePaths, string destFolder)
    {
        var watchParallel = System.Diagnostics.Stopwatch.StartNew();

        Parallel.ForEach(imagePaths, (currentImagePath) =>
        {
            using (Image image = Image.Load(currentImagePath))
            {
                image.Mutate(x => x.Resize(image.Width / 2, image.Height / 2));
                image.Save(Path.Combine(destFolder, Path.GetFileNameWithoutExtension(currentImagePath) + "_resized" + Path.GetExtension(currentImagePath)));
            }
        });

        watchParallel.Stop();
        var elapsedMs = watchParallel.ElapsedMilliseconds;
        Console.WriteLine($"Images resized parallely in {elapsedMs} ms");
    }
}

class Personne
{
    public string Nom { get; set; }

    public int Age { get; set; }

    public Personne(string nom, int age)
    {
        Nom = nom;
        Age = age;
    
    }

    public string Hello(bool isLowercase)
    {
        string message = $"hello {Nom}, you are {Age}";
        return isLowercase ? message : message.ToUpper();
    }
}
