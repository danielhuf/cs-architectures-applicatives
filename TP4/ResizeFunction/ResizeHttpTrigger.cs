using System;
using System.IO;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Azure.WebJobs;
using Microsoft.Azure.WebJobs.Extensions.Http;
using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.Logging;
using Newtonsoft.Json;
using SixLabors.ImageSharp; 
using SixLabors.ImageSharp.Processing; 
using SixLabors.ImageSharp.Formats.Jpeg; 

namespace Gonzalez.Huf.NS
{
    public static class ResizeHttpTrigger
    {
        [FunctionName("ResizeHttpTrigger")]
        public static async Task<IActionResult> Run(
            [HttpTrigger(AuthorizationLevel.Anonymous, "post", Route = null)] HttpRequest req,
            ILogger log)
        {
            log.LogInformation("C# HTTP trigger function processed a request.");
            string currentStep = "Start";
            try
            {
                string widthParamStr = req.Query["w"];
                string heightParamStr = req.Query["h"];

                if (!int.TryParse(widthParamStr, out int width) || !int.TryParse(heightParamStr, out int height))
                {
                    return new BadRequestObjectResult("Please pass width and height parameters in the query string.");
                }

                byte[]  targetImageBytes;
                using(var  msInput = new MemoryStream())
                {
                    // Récupère le corps du message en mémoire
                    await req.Body.CopyToAsync(msInput);
                    msInput.Position = 0;

                    currentStep = "Image loaded";

                    // Charge l'image            
                    using (var image = Image.Load(msInput)) 
                    {
                        // Effectue la transformation
                        image.Mutate(x => x.Resize(width, height));

                        currentStep = "Image resized";

                        // Sauvegarde en mémoire               
                        using (var msOutput = new MemoryStream())
                        {
                            image.SaveAsJpeg(msOutput);
                            targetImageBytes = msOutput.ToArray();

                            currentStep = "Image saved";
                        }
                    }
                }

                currentStep = "End";

                return new FileContentResult(targetImageBytes, "image/jpeg");
            }
            catch (Exception ex)
            {
                log.LogError($"An error occurred: {ex.Message}, at step: {currentStep}");
                return new StatusCodeResult(500);
            }
        }
    }
}
