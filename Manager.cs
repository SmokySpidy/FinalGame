using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework.Input;
using System;
using System.Collections.Generic;
using static System.Reflection.Metadata.BlobBuilder;

namespace monogame1
{
    public class Block
    {
        private Texture2D texture { get; set; }
        private Vector2 position { get; set; }

        public Block(Texture2D texture, Vector2 position)
        {
            this.texture = texture;
            this.position = position;
        }

        public void Update(GameTime gameTime)
        {
            // Update logic for block
        }

        public void Draw(SpriteBatch spriteBatch)
        {
            spriteBatch.Draw(texture, position, Color.White);
        }
    }

    public class BlockManager : DrawableGameComponent
    {
        private List<Block> blocks;
        private int currentBlockIndex;
        private Texture2D blockTexture;
        private KeyboardState prevKeyboardState;
        private Keys currentKey; // 记录当前按下的键

        public BlockManager(Game game) : base(game)
        {
            blocks = new List<Block>();
            Random random = new Random();
            currentBlockIndex = random.Next(0, 5);
        }

        protected override void LoadContent()
        {
            // Load block texture
            blockTexture = Game.Content.Load<Texture2D>("block");
        }

        public override void Update(GameTime gameTime)
        {
            // 更新键盘状态
            var keyboardState = Keyboard.GetState();

            // 检查当前按下的键并执行相应的操作
            if (keyboardState.IsKeyDown(Keys.T) && prevKeyboardState.IsKeyUp(Keys.T))
            {
                this.SwitchToPreviousBlock();
            }
            else if (keyboardState.IsKeyDown(Keys.Y) && prevKeyboardState.IsKeyUp(Keys.Y))
            {
                this.SwitchToNextBlock();
            }

            // 更新前一帧的键盘状态
            prevKeyboardState = keyboardState;

            base.Update(gameTime);
        }
        public override void Draw(GameTime gameTime)
        {
            // Draw logic for blocks
            SpriteBatch spriteBatch = Game.Services.GetService<SpriteBatch>();
            spriteBatch.Begin();
            blocks[currentBlockIndex].Draw(spriteBatch);
            spriteBatch.End();
        }

        public void AddBlock(Vector2 position)
        {
            Block newBlock = new Block(blockTexture, position);
            blocks.Add(newBlock);
        }

        public void SwitchToPreviousBlock()
        {
            currentBlockIndex--;
            if (currentBlockIndex < 0)
            {
                currentBlockIndex = blocks.Count - 1;
            }
        }

        public void SwitchToNextBlock()
        {
            currentBlockIndex++;
            if (currentBlockIndex >= blocks.Count)
            {
                currentBlockIndex = 0;
            }
        }
    }

    public class Item
    {
        private Texture2D texture;
        private Vector2 position;

        public Item(Texture2D texture, Vector2 position)
        {
            this.texture = texture;
            this.position = position;
        }

        public void Update(GameTime gameTime)
        {
            // Update logic for item
        }

        public void Draw(SpriteBatch spriteBatch)
        {
            spriteBatch.Draw(texture, position, Color.White);
        }
    }

    public class ItemManager : DrawableGameComponent
    {
        private List<Item> items;
        private Texture2D itemTexture1;
        private int currentItemIndex = 2;
        private KeyboardState prevKeyboardState;

        public ItemManager(Game game) : base(game)
        {
            items = new List<Item>();
        }

        protected override void LoadContent()
        {
            // 加载物品纹理
            Texture2D itemTexture1 = Game.Content.Load<Texture2D>("item1");
            Texture2D itemTexture2 = Game.Content.Load<Texture2D>("item2");
            Texture2D itemTexture3 = Game.Content.Load<Texture2D>("item3");
            Texture2D itemTexture4 = Game.Content.Load<Texture2D>("item4");

            // 初始化四个物品，可以根据需求调整位置
            AddItem(itemTexture1, new Vector2(100, 100));
            AddItem(itemTexture2, new Vector2(200, 200));
            AddItem(itemTexture3, new Vector2(300, 300));
            AddItem(itemTexture4, new Vector2(400, 400));
        }


        public override void Update(GameTime gameTime)
        {

            var key = Keyboard.GetState();
            // Check for input and update game logic
            if (key.IsKeyDown(Keys.U)&&prevKeyboardState.IsKeyUp(Keys.U))
            {
                this.SwitchToPreviousItem();
            }
            else if (key.IsKeyDown(Keys.I)&&prevKeyboardState.IsKeyUp(Keys.I))
            {
                this.SwitchToNextItem();
            }

            // Update other game logic
            prevKeyboardState = key;
            base.Update(gameTime);

            // Reset item switching flag after update
            //itemSwitching = false;
        }


        public override void Draw(GameTime gameTime)
        {
            // Draw logic for items
            SpriteBatch spriteBatch = Game.Services.GetService<SpriteBatch>();
            spriteBatch.Begin();
            items[currentItemIndex].Draw(spriteBatch);
            spriteBatch.End();
        }


        public void SwitchToPreviousItem()
        {
            // Switch to previous item
            currentItemIndex--;
            if (currentItemIndex < 0)
            {

                currentItemIndex = items.Count - 1;
            }
            
        }

        public void SwitchToNextItem()
        {
            // Switch to next item

            currentItemIndex = (currentItemIndex + 1) % 4;


        }


        public void AddItem(Texture2D texture, Vector2 position)
        {
            Item newItem = new Item(texture, position);
            items.Add(newItem);
        }
    }

    public class Enemy
    {
        private Texture2D enemyTexture;
        private Vector2 position;

        public Enemy(Texture2D texture, Vector2 position)
        {
            this.enemyTexture = texture;
            this.position = position;
        }

        public void Update(GameTime gameTime)
        {
            // Update logic for enemy
        }

        public void Draw(SpriteBatch spriteBatch)
        {
            spriteBatch.Draw(enemyTexture, position, Color.White);
        }
    }

    public class EnemyManager : DrawableGameComponent
    {
        private List<Enemy> enemies;
        private int currentEnemyIndex;
        private Texture2D enemyTexture;

        public EnemyManager(Game game) : base(game)
        {
            enemies = new List<Enemy>();
        }

        protected override void LoadContent()
        {
            // Load block texture
            //enemyTexture = Game.Content.Load<Texture2D>("enemy");
        }

        public override void Update(GameTime gameTime)
        {
            // Update logic for enemies
            foreach (var enemy in enemies)
            {
                enemy.Update(gameTime);
            }
        }

        public override void Draw(GameTime gameTime)
        {
            // Draw logic for enemies
            SpriteBatch spriteBatch = Game.Services.GetService<SpriteBatch>();
            spriteBatch.Begin();
            foreach (var enemy in enemies)
            {
                enemy.Draw(spriteBatch);
            }
            spriteBatch.End();
        }

        public void SwitchToPreviousEnemy()
        {
            currentEnemyIndex--;
            if (currentEnemyIndex < 0)
            {
                currentEnemyIndex = enemies.Count - 1;
            }
        }

        public void SwitchToNextEnemy()
        {
            currentEnemyIndex++;
            if (currentEnemyIndex >= enemies.Count)
            {
                currentEnemyIndex = 0;
            }
        }

        public void AddEnemy(Texture2D texture, Vector2 position)
        {
            Enemy newEnemy = new Enemy(texture, position);
            enemies.Add(newEnemy);
        }
    }
}
